package com.yunming.tea.service.impl; // 声明包路径，属于服务实现层包

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper; // 导入MyBatis-Plus的Lambda查询包装器
import com.yunming.tea.entity.TeaCategory; // 导入茶品分类实体类
import com.yunming.tea.mapper.TeaCategoryMapper; // 导入茶品分类数据访问层
import com.yunming.tea.service.TeaCategoryService; // 导入茶品分类服务接口
import com.yunming.tea.vo.CategoryVO; // 导入分类视图对象
import org.springframework.stereotype.Service; // 导入Spring的Service注解

import java.util.ArrayList; // 导入ArrayList集合类
import java.util.List; // 导入List接口
import java.util.Map; // 导入Map接口
import java.util.stream.Collectors; // 导入Collectors工具类

/**
 * 茶品分类服务实现类
 * <p>
 * 实现了{@link TeaCategoryService}接口中定义的茶品分类相关业务逻辑。
 * 主要功能包括：
 * <ul>
 *   <li>获取所有分类（平铺列表）：返回不包含子节点的分类列表，用于下拉选择</li>
 *   <li>获取分类树形结构：递归构建多级分类树，用于导航菜单</li>
 *   <li>根据ID获取分类实体：直接查询数据库原始记录</li>
 * </ul>
 * <p>
 * 数据过滤规则：自动排除测试数据，包括：
 * <ul>
 *   <li>分类名称以"test_"开头的分类</li>
 *   <li>分类名称等于"测试分类"的分类</li>
 *   <li>分类描述包含"auto-test"的分类</li>
 * </ul>
 * <p>
 * 树形结构构建逻辑：
 * 首先获取所有分类，然后按parentId分组，从根节点（parentId为null或0）开始，
 * 递归查找子节点并组装成树形结构。支持任意层级的嵌套。
 *
 * @author yunming
 * @see TeaCategoryService
 */
@Service // 将该类标记为Spring的Service组件
public class TeaCategoryServiceImpl implements TeaCategoryService {

    private final TeaCategoryMapper categoryMapper; // 茶品分类数据访问对象

    /**
     * 构造函数注入依赖
     *
     * @param categoryMapper 茶品分类数据访问对象
     */
    public TeaCategoryServiceImpl(TeaCategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper; // 注入分类Mapper
    }

    /**
     * 获取所有分类（平铺列表）
     * <p>
     * 查询所有有效的茶品分类（排除测试数据），按排序字段升序排列。
     * 返回不包含children子节点的平铺结构，适用于下拉选择框等场景。
     *
     * @return 分类视图对象列表，按sort升序排列
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public List<CategoryVO> getAllCategories() {
        // 构建查询条件
        LambdaQueryWrapper<TeaCategory> wrapper = new LambdaQueryWrapper<>(); // 创建查询包装器
        // 排除描述包含"auto-test"的分类（描述为空或描述不包含auto-test）
        wrapper.and(w -> w.isNull(TeaCategory::getDescription) // 条件：描述字段为null
                .or() // 或
                .notLike(TeaCategory::getDescription, "auto-test")); // 描述不包含"auto-test"字符串
        wrapper.notLike(TeaCategory::getName, "test_"); // 条件：名称不以"test_"开头
        wrapper.ne(TeaCategory::getName, "测试分类"); // 条件：名称不等于"测试分类"
        wrapper.orderByAsc(TeaCategory::getSort); // 排序：按排序字段升序（sort值小的在前）
        List<TeaCategory> categories = categoryMapper.selectList(wrapper); // 执行查询

        // 将实体列表转换为视图对象列表（平铺，不含子节点）
        return categories.stream() // 转为流
                .map(this::toVO) // 每个实体映射为视图对象
                .collect(Collectors.toList()); // 收集为List
    }

    /**
     * 获取分类树形结构
     * <p>
     * 查询所有有效的茶品分类，递归构建树形层级结构。
     * 构建流程：
     * <ol>
     *   <li>查询所有分类（排除测试数据），按sort升序</li>
     *   <li>按parentId分组，建立父子关系映射</li>
     *   <li>从根节点（parentId为null或0）开始，递归构建子树</li>
     * </ol>
     * 返回的根节点包含完整的children子树，适合前端导航菜单使用。
     *
     * @return 分类树形视图对象列表，根节点包含完整的子分类树
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public List<CategoryVO> getCategoryTree() {
        // 第1步：查询所有有效分类（排除测试数据），按sort升序
        List<TeaCategory> all = categoryMapper.selectList( // 查询所有分类
                new LambdaQueryWrapper<TeaCategory>() // 创建查询包装器
                        .and(w -> w.isNull(TeaCategory::getDescription) // 描述为空
                                .or() // 或
                                .notLike(TeaCategory::getDescription, "auto-test")) // 描述不包含auto-test
                        .notLike(TeaCategory::getName, "test_") // 名称不以test_开头
                        .ne(TeaCategory::getName, "测试分类") // 名称不等于测试分类
                        .orderByAsc(TeaCategory::getSort)); // 按排序字段升序

        // 第2步：按parentId分组，筛选掉根节点（parentId不为null且不为0的）
        // 构建 Map<parentId, List<TeaCategory>>，方便快速查找某个父节点下的所有子节点
        Map<Long, List<TeaCategory>> parentMap = all.stream() // 转为流
                .filter(c -> c.getParentId() != null && c.getParentId() != 0) // 过滤：只保留有父节点的子分类
                .collect(Collectors.groupingBy(TeaCategory::getParentId)); // 按父节点ID分组

        // 第3步：构建根节点列表（parentId为null或0的即为根节点）
        List<CategoryVO> roots = new ArrayList<>(); // 创建根节点列表
        for (TeaCategory c : all) { // 遍历所有分类
            if (c.getParentId() == null || c.getParentId() == 0) { // 判断是否为根节点
                CategoryVO vo = toVO(c); // 将根节点实体转为视图对象
                vo.setChildren(buildChildren(c.getId(), parentMap)); // 递归构建子分类树
                roots.add(vo); // 添加到根节点列表
            }
        }
        return roots; // 返回分类树根节点列表
    }

    /**
     * 递归构建子分类树
     * <p>
     * 根据parentId从parentMap中查找子分类列表，
     * 对每个子分类继续递归构建其下级子分类。
     * 如果当前节点没有子分类，返回null（前端可据此判断是叶子节点）。
     *
     * @param parentId 父分类ID
     * @param parentMap 父节点到子节点列表的映射Map
     * @return 子分类视图对象列表，如果没有子节点则返回null
     */
    private List<CategoryVO> buildChildren(Long parentId, Map<Long, List<TeaCategory>> parentMap) {
        List<TeaCategory> children = parentMap.get(parentId); // 从Map中获取该父节点下的子分类列表
        if (children == null) return null; // 如果没有子节点，返回null（表示叶子节点）
        // 遍历子分类列表，递归构建子树
        return children.stream().map(c -> { // 遍历并映射每个子分类
            CategoryVO vo = toVO(c); // 将子分类实体转为视图对象
            vo.setChildren(buildChildren(c.getId(), parentMap)); // 递归构建该子分类的下一级子分类
            return vo; // 返回构建好的视图对象
        }).collect(Collectors.toList()); // 收集为List
    }

    /**
     * 根据ID获取分类实体
     * <p>
     * 直接根据主键ID查询分类的完整实体信息。
     *
     * @param id 分类ID
     * @return 茶品分类实体对象，如果不存在则返回null
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public TeaCategory getById(Long id) {
        return categoryMapper.selectById(id); // 根据主键ID查询分类实体，返回实体或null
    }

    /**
     * 将分类实体对象转换为视图对象
     * <p>
     * 提取分类实体的关键字段到视图对象中，用于前端展示。
     *
     * @param c 分类实体对象
     * @return 分类视图对象
     */
    private CategoryVO toVO(TeaCategory c) { // 私有方法，实体转视图
        CategoryVO vo = new CategoryVO(); // 创建视图对象
        vo.setId(c.getId()); // 设置分类ID
        vo.setName(c.getName()); // 设置分类名称
        vo.setParentId(c.getParentId()); // 设置父分类ID（null表示根节点）
        vo.setSort(c.getSort()); // 设置排序值
        vo.setIcon(c.getIcon()); // 设置分类图标URL
        vo.setDescription(c.getDescription()); // 设置分类描述
        vo.setCreateTime(c.getCreateTime()); // 设置创建时间
        return vo; // 返回视图对象
    }
}
