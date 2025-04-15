package com.example.backend.service.impl;

import com.example.backend.entity.Resource;
import com.example.backend.repository.ResourceRepository;
import com.example.backend.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 资源服务实现类
 */
@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

    // 文件上传目录
    private final String uploadDir = "uploads/resources";

    @Override
    public Page<Resource> getResources(Map<String, Object> filters, Pageable pageable) {
        if (filters == null || filters.isEmpty()) {
            return resourceRepository.findAll(pageable);
        }

        if (filters.containsKey("type") && filters.get("type") != null) {
            return resourceRepository.findByResourceType((String) filters.get("type"), pageable);
        }

        if (filters.containsKey("category") && filters.get("category") != null) {
            return resourceRepository.findByCategory((String) filters.get("category"), pageable);
        }

        if (filters.containsKey("keyword") && filters.get("keyword") != null) {
            return resourceRepository.searchByKeyword((String) filters.get("keyword"), pageable);
        }

        return resourceRepository.findAll(pageable);
    }

    @Override
    public Resource getResourceById(Long id) {
        Optional<Resource> resource = resourceRepository.findById(id);
        if (resource.isPresent()) {
            // 增加查看次数
            incrementViewCount(id);
            return resource.get();
        }
        return null;
    }

    @Override
    @Transactional
    public Resource createResource(Resource resource) {
        resource.setCreateTime(new Date());
        resource.setUpdateTime(new Date());
        resource.setDownloadCount(0);
        resource.setViewCount(0);
        return resourceRepository.save(resource);
    }

    @Override
    @Transactional
    public Resource updateResource(Long id, Resource resource) {
        Optional<Resource> existingResource = resourceRepository.findById(id);
        if (!existingResource.isPresent()) {
            return null;
        }

        Resource oldResource = existingResource.get();
        resource.setId(id);
        resource.setCreateTime(oldResource.getCreateTime());
        resource.setUpdateTime(new Date());
        resource.setDownloadCount(oldResource.getDownloadCount());
        resource.setViewCount(oldResource.getViewCount());

        return resourceRepository.save(resource);
    }

    @Override
    @Transactional
    public boolean deleteResource(Long id) {
        if (!resourceRepository.existsById(id)) {
            return false;
        }

        resourceRepository.deleteById(id);
        return true;
    }

    @Override
    public List<Resource> getHotResources(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return resourceRepository.findHotResources(pageable);
    }

    @Override
    public List<Resource> getLatestResources(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return resourceRepository.findLatestResources(pageable);
    }

    @Override
    public List<String> getResourceCategories() {
        return resourceRepository.findAllCategories();
    }

    @Override
    public List<String> getResourceTypes() {
        return resourceRepository.findAllResourceTypes();
    }

    @Override
    public List<Resource> getResourcesByCreator(String creatorEmail) {
        return resourceRepository.findByCreatorEmail(creatorEmail);
    }

    @Override
    public List<Resource> getResourcesByCourse(Long courseId) {
        return resourceRepository.findByCourseId(courseId);
    }

    @Override
    public String getResourceDownloadUrl(Long id) {
        Optional<Resource> resource = resourceRepository.findById(id);
        if (!resource.isPresent()) {
            return null;
        }

        Resource res = resource.get();

        // 如果是文件类型，返回文件URL
        if (res.getFileUrl() != null && !res.getFileUrl().isEmpty()) {
            return res.getFileUrl();
        }

        // 如果是外部链接，返回外部链接
        if (res.getExternalLink() != null && !res.getExternalLink().isEmpty()) {
            return res.getExternalLink();
        }

        return null;
    }

    @Override
    @Transactional
    public boolean incrementDownloadCount(Long id) {
        Optional<Resource> resource = resourceRepository.findById(id);
        if (!resource.isPresent()) {
            return false;
        }

        Resource res = resource.get();
        res.setDownloadCount(res.getDownloadCount() + 1);
        resourceRepository.save(res);

        return true;
    }

    @Override
    @Transactional
    public boolean incrementViewCount(Long id) {
        Optional<Resource> resource = resourceRepository.findById(id);
        if (!resource.isPresent()) {
            return false;
        }

        Resource res = resource.get();
        res.setViewCount(res.getViewCount() + 1);
        resourceRepository.save(res);

        return true;
    }

    @Override
    public Page<Resource> searchResources(String keyword, Pageable pageable) {
        return resourceRepository.searchByKeyword(keyword, pageable);
    }

    @Override
    public Map<String, Object> getTeacherResources(Long teacherId, String keyword, String type, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Resource> resourcePage;

        if (keyword != null && !keyword.isEmpty()) {
            resourcePage = resourceRepository.findByCreatorIdAndKeyword(teacherId, keyword, pageable);
        } else if (type != null && !type.isEmpty()) {
            resourcePage = resourceRepository.findByCreatorIdAndResourceType(teacherId, type, pageable);
        } else {
            resourcePage = resourceRepository.findByCreatorId(teacherId, pageable);
        }

        List<Resource> resources = resourcePage.getContent();

        Map<String, Object> result = new HashMap<>();
        result.put("content", resources);
        result.put("totalElements", resourcePage.getTotalElements());
        result.put("totalPages", resourcePage.getTotalPages());
        result.put("currentPage", page);
        result.put("size", size);

        return result;
    }

    @Override
    public Map<String, Object> getCourseResources(Long courseId, String keyword, String type, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Resource> resourcePage;

        if (keyword != null && !keyword.isEmpty()) {
            resourcePage = resourceRepository.findByCourseIdAndKeyword(courseId, keyword, pageable);
        } else if (type != null && !type.isEmpty()) {
            resourcePage = resourceRepository.findByCourseIdAndResourceType(courseId, type, pageable);
        } else {
            resourcePage = resourceRepository.findByCourseId(courseId, pageable);
        }

        List<Resource> resources = resourcePage.getContent();

        Map<String, Object> result = new HashMap<>();
        result.put("content", resources);
        result.put("totalElements", resourcePage.getTotalElements());
        result.put("totalPages", resourcePage.getTotalPages());
        result.put("currentPage", page);
        result.put("size", size);

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> uploadResource(Long teacherId, MultipartFile file, Map<String, String> data) throws IOException {
        // 创建上传目录
        File uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs();
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

        // 保存文件
        Path filePath = Paths.get(uploadDir, uniqueFilename);
        Files.copy(file.getInputStream(), filePath);

        // 创建资源记录
        Resource resource = new Resource();
        resource.setTitle(data.getOrDefault("title", originalFilename));
        resource.setDescription(data.getOrDefault("description", ""));
        resource.setResourceType(data.getOrDefault("resourceType", "文档"));
        resource.setCategory(data.getOrDefault("category", "课程资料"));
        resource.setFileUrl("/" + uploadDir + "/" + uniqueFilename);
        resource.setFileName(originalFilename);
        resource.setFileSize(file.getSize());
        resource.setFileType(fileExtension);
        resource.setCreatorId(teacherId);
        resource.setCreatorEmail(data.get("creatorEmail"));
        resource.setCreatorName(data.getOrDefault("creatorName", ""));

        // 如果指定了课程ID，关联到课程
        if (data.containsKey("courseId")) {
            resource.setCourseId(Long.parseLong(data.get("courseId")));
        }

        // 保存资源
        Resource savedResource = createResource(resource);

        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("id", savedResource.getId());
        result.put("title", savedResource.getTitle());
        result.put("description", savedResource.getDescription());
        result.put("resourceType", savedResource.getResourceType());
        result.put("category", savedResource.getCategory());
        result.put("fileUrl", savedResource.getFileUrl());
        result.put("fileName", savedResource.getFileName());
        result.put("fileSize", savedResource.getFileSize());
        result.put("fileType", savedResource.getFileType());
        result.put("creatorId", savedResource.getCreatorId());
        result.put("creatorEmail", savedResource.getCreatorEmail());
        result.put("creatorName", savedResource.getCreatorName());
        result.put("courseId", savedResource.getCourseId());
        result.put("createTime", savedResource.getCreateTime());

        return result;
    }

    @Override
    public Map<String, Object> getResourceDetail(Long id) {
        Resource resource = getResourceById(id);
        if (resource == null) {
            return null;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", resource.getId());
        result.put("title", resource.getTitle());
        result.put("description", resource.getDescription());
        result.put("resourceType", resource.getResourceType());
        result.put("category", resource.getCategory());
        result.put("fileUrl", resource.getFileUrl());
        result.put("fileName", resource.getFileName());
        result.put("fileSize", resource.getFileSize());
        result.put("fileType", resource.getFileType());
        result.put("externalLink", resource.getExternalLink());
        result.put("creatorId", resource.getCreatorId());
        result.put("creatorEmail", resource.getCreatorEmail());
        result.put("creatorName", resource.getCreatorName());
        result.put("courseId", resource.getCourseId());
        result.put("createTime", resource.getCreateTime());
        result.put("updateTime", resource.getUpdateTime());
        result.put("downloadCount", resource.getDownloadCount());
        result.put("viewCount", resource.getViewCount());

        return result;
    }

    @Override
    @Transactional
    public Map<String, Object> updateResource(Long id, Map<String, Object> data) {
        Optional<Resource> resourceOpt = resourceRepository.findById(id);
        if (!resourceOpt.isPresent()) {
            return null;
        }

        Resource resource = resourceOpt.get();

        // 更新资源信息
        if (data.containsKey("title")) {
            resource.setTitle((String) data.get("title"));
        }

        if (data.containsKey("description")) {
            resource.setDescription((String) data.get("description"));
        }

        if (data.containsKey("resourceType")) {
            resource.setResourceType((String) data.get("resourceType"));
        }

        if (data.containsKey("category")) {
            resource.setCategory((String) data.get("category"));
        }

        if (data.containsKey("externalLink")) {
            resource.setExternalLink((String) data.get("externalLink"));
        }

        if (data.containsKey("courseId")) {
            Object courseIdObj = data.get("courseId");
            if (courseIdObj != null) {
                if (courseIdObj instanceof Number) {
                    resource.setCourseId(((Number) courseIdObj).longValue());
                } else if (courseIdObj instanceof String) {
                    try {
                        resource.setCourseId(Long.parseLong((String) courseIdObj));
                    } catch (NumberFormatException e) {
                        // 忽略无效的课程ID
                    }
                }
            } else {
                resource.setCourseId(null);
            }
        }

        resource.setUpdateTime(new Date());

        // 保存更新
        Resource updatedResource = resourceRepository.save(resource);

        // 返回更新后的资源
        return getResourceDetail(updatedResource.getId());
    }

    @Override
    public boolean isTeacherResource(Long teacherId, Long resourceId) {
        Optional<Resource> resourceOpt = resourceRepository.findById(resourceId);
        if (!resourceOpt.isPresent()) {
            return false;
        }

        Resource resource = resourceOpt.get();
        return resource.getCreatorId() != null && resource.getCreatorId().equals(teacherId);
    }

    @Override
    @Transactional
    public void addResourceToCourse(Long resourceId, Long courseId) {
        Optional<Resource> resourceOpt = resourceRepository.findById(resourceId);
        if (resourceOpt.isPresent()) {
            Resource resource = resourceOpt.get();
            resource.setCourseId(courseId);
            resource.setUpdateTime(new Date());
            resourceRepository.save(resource);
        }
    }

    @Override
    @Transactional
    public void removeResourceFromCourse(Long resourceId, Long courseId) {
        Optional<Resource> resourceOpt = resourceRepository.findById(resourceId);
        if (resourceOpt.isPresent()) {
            Resource resource = resourceOpt.get();
            if (resource.getCourseId() != null && resource.getCourseId().equals(courseId)) {
                resource.setCourseId(null);
                resource.setUpdateTime(new Date());
                resourceRepository.save(resource);
            }
        }
    }

    @Override
    public Map<String, Object> getTeacherResourceStatistics(Long teacherId) {
        Map<String, Object> statistics = new HashMap<>();

        // 统计总资源数量
        long totalResources = resourceRepository.countByCreatorId(teacherId);
        statistics.put("totalResources", totalResources);

        // 按类型统计资源数量
        Map<String, Long> resourcesByType = new HashMap<>();
        List<String> resourceTypes = resourceRepository.findAllResourceTypes();
        for (String type : resourceTypes) {
            long count = resourceRepository.countByCreatorIdAndResourceType(teacherId, type);
            if (count > 0) {
                resourcesByType.put(type, count);
            }
        }
        statistics.put("resourcesByType", resourcesByType);

        // 获取最近上传的资源
        Pageable pageable = PageRequest.of(0, 5);
        Page<Resource> recentResources = resourceRepository.findByCreatorId(teacherId, pageable);
        statistics.put("recentResources", recentResources.getContent());

        // 获取最受欢迎的资源（按下载量排序）
        Pageable downloadPageable = PageRequest.of(0, 5, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "downloadCount"));
        Page<Resource> popularResources = resourceRepository.findByCreatorId(teacherId, downloadPageable);
        statistics.put("popularResources", popularResources.getContent());

        return statistics;
    }

    @Override
    @Transactional
    public void batchDeleteResources(List<Long> resourceIds) {
        if (resourceIds == null || resourceIds.isEmpty()) {
            return;
        }

        for (Long resourceId : resourceIds) {
            Optional<Resource> resourceOpt = resourceRepository.findById(resourceId);
            if (resourceOpt.isPresent()) {
                Resource resource = resourceOpt.get();

                // 删除物理文件（如果有）
                if (resource.getFileUrl() != null && !resource.getFileUrl().isEmpty()) {
                    try {
                        String filePath = resource.getFileUrl().replace("/api/resources/download/", "");
                        Path path = Paths.get(uploadDir, filePath);
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        // 忽略文件删除错误，继续删除数据库记录
                        e.printStackTrace();
                    }
                }

                // 删除数据库记录
                resourceRepository.delete(resource);
            }
        }
    }

    @Override
    @Transactional
    public void batchAddResourcesToCourse(List<Long> resourceIds, Long courseId) {
        if (resourceIds == null || resourceIds.isEmpty() || courseId == null) {
            return;
        }

        for (Long resourceId : resourceIds) {
            Optional<Resource> resourceOpt = resourceRepository.findById(resourceId);
            if (resourceOpt.isPresent()) {
                Resource resource = resourceOpt.get();
                resource.setCourseId(courseId);
                resource.setUpdateTime(new Date());
                resourceRepository.save(resource);
            }
        }
    }
    
    @Override
    public Map<String, Object> getTeacherResourcesByType(Long teacherId, String type, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Resource> resourcePage = resourceRepository.findByCreatorIdAndResourceType(teacherId, type, pageable);
        
        Map<String, Object> result = new HashMap<>();
        result.put("content", resourcePage.getContent());
        result.put("totalElements", resourcePage.getTotalElements());
        result.put("totalPages", resourcePage.getTotalPages());
        result.put("currentPage", page);
        result.put("size", size);
        
        return result;
    }
}