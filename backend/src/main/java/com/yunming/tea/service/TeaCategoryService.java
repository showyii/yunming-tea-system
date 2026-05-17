package com.yunming.tea.service; // 声明包路径，属于服务层接口包

import com.yunming.tea.entity.TeaCategory; // 导入茶品分类实体类，对应数据库中的分类表
import com.yunming.tea.vo.CategoryVO; // 导入分类视图对象，用于返回给前端的分类数据结构

import java.util.List; // 导入List集合类，用于返回分类列表

/**
 * 茶品分类服务接口
 * <p>
 * 该接口定义了茶品分类相关的业务操作规范，包括：
 * 1. 获取所有分类的平铺列表（用于下拉选择等场景）
 * 2. 获取分类的树形结构（用于导航菜单展示）
 * 3. 根据ID获取单个分类信息
 * <p>
 * 分类支持多级结构，通过parentId字段实现父子关系。
 * 树形结构查询会递归构建层级关系，返回完整的分类树。
 * 查询时会自动过滤掉测试数据（名称以"test_"开头或名称为"测试分类"或描述包含"auto-test"的分类）。
 *
 * @author yunming
 * @see com.yunming.tea.service.impl.TeaCategoryServiceImpl
 */
public interface TeaCategoryService {

    /**
     * 获取所有分类（平铺列表）
     * <p>
     * 查询所有有效的茶品分类，按排序字段升序排列，
     * 返回不包含子节点的平铺结构列表。
     * 会自动排除测试分类数据。
     *
     * @return 分类视图对象列表，每个分类不包含children子节点
     */
    List<CategoryVO> getAllCategories(); // 获取所有分类列表方法声明

    /**
     * 获取分类树形结构
     * <p>
     * 查询所有有效的茶品分类并构建树形层级结构，
     * 顶层为parentId为null或0的分类，每个父分类下包含其子分类列表。
     * 支持多级嵌套，适合前端导航菜单或级联选择器使用。
     * 会自动排除测试分类数据。
     *
     * @return 分类树形结构视图对象列表，顶层分类包含children子分类
     */
    List<CategoryVO> getCategoryTree(); // 获取分类树形结构方法声明

    /**
     * 根据ID获取分类实体
     * <p>
     * 直接根据主键ID查询分类的完整实体信息，
     * 返回数据库中的原始记录，包含所有字段。
     *
     * @param id 分类ID，用于唯一标识一个分类
     * @return 茶品分类实体对象，如果分类不存在则返回null
     */
    TeaCategory getById(Long id); // 根据ID获取分类方法声明
}
