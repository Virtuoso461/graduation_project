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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 资源服务实现类
 */
@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceRepository resourceRepository;

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
} 