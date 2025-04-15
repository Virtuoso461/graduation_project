package com.example.backend.service;

import com.example.backend.entity.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 资源服务接口
 */
public interface ResourceService {

    /**
     * 获取资源列表（分页）
     *
     * @param filters 过滤条件
     * @param pageable 分页参数
     * @return 资源分页结果
     */
    Page<Resource> getResources(Map<String, Object> filters, Pageable pageable);

    /**
     * 根据ID获取资源
     *
     * @param id 资源ID
     * @return 资源详情
     */
    Resource getResourceById(Long id);

    /**
     * 创建新资源
     *
     * @param resource 资源信息
     * @return 创建后的资源
     */
    Resource createResource(Resource resource);

    /**
     * 更新资源
     *
     * @param id 资源ID
     * @param resource 资源信息
     * @return 更新后的资源
     */
    Resource updateResource(Long id, Resource resource);

    /**
     * 删除资源
     *
     * @param id 资源ID
     * @return 操作是否成功
     */
    boolean deleteResource(Long id);

    /**
     * 获取热门资源
     *
     * @param limit 数量限制
     * @return 热门资源列表
     */
    List<Resource> getHotResources(int limit);

    /**
     * 获取最新资源
     *
     * @param limit 数量限制
     * @return 最新资源列表
     */
    List<Resource> getLatestResources(int limit);

    /**
     * 获取资源分类列表
     *
     * @return 分类列表
     */
    List<String> getResourceCategories();

    /**
     * 获取资源类型列表
     *
     * @return 类型列表
     */
    List<String> getResourceTypes();

    /**
     * 根据创建者获取资源
     *
     * @param creatorEmail 创建者邮箱
     * @return 资源列表
     */
    List<Resource> getResourcesByCreator(String creatorEmail);

    /**
     * 根据课程ID获取资源
     *
     * @param courseId 课程ID
     * @return 资源列表
     */
    List<Resource> getResourcesByCourse(Long courseId);

    /**
     * 获取资源下载URL
     *
     * @param id 资源ID
     * @return 下载URL
     */
    String getResourceDownloadUrl(Long id);

    /**
     * 增加资源下载次数
     *
     * @param id 资源ID
     * @return 是否成功
     */
    boolean incrementDownloadCount(Long id);

    /**
     * 增加资源查看次数
     *
     * @param id 资源ID
     * @return 是否成功
     */
    boolean incrementViewCount(Long id);

    /**
     * 搜索资源
     *
     * @param keyword 关键词
     * @param pageable 分页参数
     * @return 资源分页结果
     */
    Page<Resource> searchResources(String keyword, Pageable pageable);

    /**
     * 获取教师的所有教学资源
     *
     * @param teacherId 教师ID
     * @param keyword 搜索关键词
     * @param type 资源类型
     * @param page 页码
     * @param size 每页数量
     * @return 带分页信息的资源列表
     */
    Map<String, Object> getTeacherResources(Long teacherId, String keyword, String type, int page, int size);

    /**
     * 获取课程的所有教学资源
     *
     * @param courseId 课程ID
     * @param keyword 搜索关键词
     * @param type 资源类型
     * @param page 页码
     * @param size 每页数量
     * @return 带分页信息的资源列表
     */
    Map<String, Object> getCourseResources(Long courseId, String keyword, String type, int page, int size);

    /**
     * 上传教学资源
     *
     * @param teacherId 教师ID
     * @param file 文件
     * @param data 资源元数据
     * @return 上传后的资源信息
     * @throws IOException 文件处理异常
     */
    Map<String, Object> uploadResource(Long teacherId, MultipartFile file, Map<String, String> data) throws IOException;

    /**
     * 获取资源详情
     *
     * @param id 资源ID
     * @return 资源详情
     */
    Map<String, Object> getResourceDetail(Long id);

    /**
     * 更新资源信息
     *
     * @param id 资源ID
     * @param data 资源数据
     * @return 更新后的资源信息
     */
    Map<String, Object> updateResource(Long id, Map<String, Object> data);

    /**
     * 检查资源是否属于指定教师
     *
     * @param teacherId 教师ID
     * @param resourceId 资源ID
     * @return 如果资源属于该教师，则返回true
     */
    boolean isTeacherResource(Long teacherId, Long resourceId);

    /**
     * 将资源添加到课程
     *
     * @param resourceId 资源ID
     * @param courseId 课程ID
     */
    void addResourceToCourse(Long resourceId, Long courseId);

    /**
     * 从课程中移除资源
     *
     * @param resourceId 资源ID
     * @param courseId 课程ID
     */
    void removeResourceFromCourse(Long resourceId, Long courseId);

    /**
     * 获取教师的资源统计数据
     *
     * @param teacherId 教师ID
     * @return 统计数据
     */
    Map<String, Object> getTeacherResourceStatistics(Long teacherId);

    /**
     * 批量删除资源
     *
     * @param resourceIds 资源ID列表
     */
    void batchDeleteResources(List<Long> resourceIds);

    /**
     * 批量将资源添加到课程
     *
     * @param resourceIds 资源ID列表
     * @param courseId 课程ID
     */
    void batchAddResourcesToCourse(List<Long> resourceIds, Long courseId);

    /**
     * 按类型获取教师的资源
     *
     * @param teacherId 教师ID
     * @param type 资源类型
     * @param page 页码
     * @param size 每页大小
     * @return 资源列表及分页信息
     */
    Map<String, Object> getTeacherResourcesByType(Long teacherId, String type, int page, int size);
}