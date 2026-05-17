package com.yunming.tea.controller.admin; // 包声明：管理后台控制器层

import com.yunming.tea.common.Result; // 统一响应结果封装类
import com.yunming.tea.entity.TeaCategory; // 茶分类实体类
import com.yunming.tea.mapper.TeaCategoryMapper; // 茶分类数据访问层（MyBatis-Plus Mapper）
import com.yunming.tea.service.TeaCategoryService; // 茶分类业务逻辑服务
import com.yunming.tea.vo.CategoryVO; // 分类视图对象
import org.springframework.web.bind.annotation.*; // Spring MVC 注解：包括 RestController、RequestMapping、GetMapping 等

import java.util.List; // Java 集合框架的 List 接口

/**
 * 管理员 - 分类管理控制器
 *
 * 负责管理后台的茶产品分类管理功能，包括：
 * - 查看所有分类列表
 * - 查看分类树形结构
 * - 查看单个分类详情
 * - 创建新分类
 * - 编辑分类信息
 * - 删除分类
 *
 * 分类支持多级结构（父分类-子分类），用于茶产品的归属组织。
 *
 * 映射路径：/api/admin/categories
 */
@RestController // 标识这是一个 RESTful 控制器，所有返回值自动序列化为 JSON
@RequestMapping("/api/admin/categories") // 将控制器映射到 /api/admin/categories 路径下
public class AdminCategoryController {

    private final TeaCategoryService categoryService; // 茶分类业务服务，用于查询操作
    private final TeaCategoryMapper categoryMapper; // 茶分类 Mapper，用于直接的增删改操作

    /**
     * 构造器注入 TeaCategoryService 和 TeaCategoryMapper
     *
     * @param categoryService 茶分类业务服务实例，用于列表、树形和详情的查询
     * @param categoryMapper  茶分类数据访问层实例，用于创建、更新和删除操作
     */
    public AdminCategoryController(TeaCategoryService categoryService, TeaCategoryMapper categoryMapper) { // 通过构造器注入两个依赖
        this.categoryService = categoryService; // 将注入的分类服务赋值给成员变量
        this.categoryMapper = categoryMapper; // 将注入的 Mapper 赋值给成员变量
    }

    /**
     * 获取所有分类列表
     *
     * 返回系统中所有的茶产品分类，按排序字段升序排列。
     *
     * 请求方式：GET /api/admin/categories
     *
     * @return Result 包含所有分类列表的统一响应结果
     */
    @GetMapping // 处理 HTTP GET 请求，映射到 /api/admin/categories
    public Result<List<CategoryVO>> list() { // 获取全部分类列表的方法
        return Result.success(categoryService.getAllCategories()); // 调用业务层获取全部分类并包装为成功响应
    }

    /**
     * 获取分类树形结构
     *
     * 返回分类的树形层级结构，展示父分类与子分类的嵌套关系。
     * 用于管理后台的分类选择器和分类管理界面。
     *
     * 请求方式：GET /api/admin/categories/tree
     *
     * @return Result 包含分类树形结构的统一响应结果
     */
    @GetMapping("/tree") // 处理 HTTP GET 请求，映射到 /api/admin/categories/tree
    public Result<List<CategoryVO>> tree() { // 获取分类树的方法
        return Result.success(categoryService.getCategoryTree()); // 调用业务层获取分类树并包装为成功响应
    }

    /**
     * 根据 ID 获取单个分类详情
     *
     * 返回指定分类的完整信息，包括分类名称、图标、父分类 ID、排序权重等。
     *
     * 请求方式：GET /api/admin/categories/{id}
     *
     * @param id 分类 ID，从 URL 路径中获取
     * @return Result 包含分类实体信息的统一响应结果
     */
    @GetMapping("/{id}") // 处理 HTTP GET 请求，映射到 /api/admin/categories/{id}
    public Result<TeaCategory> getById(@PathVariable Long id) { // @PathVariable 将 URL 中的 {id} 绑定到方法参数
        return Result.success(categoryService.getById(id)); // 调用业务层根据 ID 获取分类信息并包装为成功响应
    }

    /**
     * 创建新分类
     *
     * 在系统中创建一个新的茶产品分类。需要指定分类名称、父分类 ID（可选，用于创建子分类）、
     * 排序权重等。创建后该分类即对新商品可见。
     *
     * 请求方式：POST /api/admin/categories
     *
     * @param category 分类实体对象，由请求体 JSON 自动反序列化
     * @return Result 包含新创建的分类实体（含自动生成的 ID）的统一响应结果
     */
    @PostMapping // 处理 HTTP POST 请求，映射到 /api/admin/categories
    public Result<TeaCategory> create(@RequestBody TeaCategory category) { // @RequestBody 将请求体 JSON 反序列化为 TeaCategory 实体
        categoryMapper.insert(category); // 使用 Mapper 直接将分类实体插入数据库
        return Result.success(category); // 返回新创建的分类实体（此时 ID 已由数据库自动填充）
    }

    /**
     * 编辑分类信息
     *
     * 更新指定分类的信息。可以将路径中的 ID 与请求体中的实体合并后进行更新。
     *
     * 请求方式：PUT /api/admin/categories/{id}
     *
     * @param id       分类 ID，从 URL 路径中获取，标识要修改的分类
     * @param category 分类实体对象，包含要更新的字段，由请求体 JSON 自动反序列化
     * @return Result 包含更新成功提示的统一响应结果
     */
    @PutMapping("/{id}") // 处理 HTTP PUT 请求，映射到 /api/admin/categories/{id}
    public Result<String> update(@PathVariable Long id, @RequestBody TeaCategory category) { // @PathVariable 从 URL 获取 ID，@RequestBody 反序列化请求体
        category.setId(id); // 将路径参数中的 ID 设置到分类实体上，确保更新正确的记录
        categoryMapper.updateById(category); // 使用 Mapper 根据 ID 更新分类记录
        return Result.success("更新成功"); // 返回更新成功的提示信息
    }

    /**
     * 删除分类
     *
     * 根据分类 ID 从数据库中删除指定的分类。删除前应注意：
     * - 如果该分类有子分类，可能需要先处理子分类
     * - 如果该分类下有商品，可能需要先将商品重新归类
     *
     * 请求方式：DELETE /api/admin/categories/{id}
     *
     * @param id 分类 ID，从 URL 路径中获取，标识要删除的分类
     * @return Result 包含删除成功提示的统一响应结果
     */
    @DeleteMapping("/{id}") // 处理 HTTP DELETE 请求，映射到 /api/admin/categories/{id}
    public Result<String> delete(@PathVariable Long id) { // @PathVariable 将 URL 中的 {id} 绑定到方法参数
        categoryMapper.deleteById(id); // 使用 Mapper 根据 ID 删除分类记录
        return Result.success("删除成功"); // 返回删除成功的提示信息
    }
}
