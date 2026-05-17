package com.yunming.tea.controller; // 包声明：控制器层

import com.yunming.tea.common.Result; // 统一响应结果封装类
import com.yunming.tea.service.TeaCategoryService; // 茶分类业务逻辑服务
import com.yunming.tea.vo.CategoryVO; // 分类视图对象
import org.springframework.web.bind.annotation.GetMapping; // GET 请求映射注解
import org.springframework.web.bind.annotation.RequestMapping; // 类级别请求路径映射注解
import org.springframework.web.bind.annotation.RestController; // REST 控制器标识注解

import java.util.List; // Java 集合框架的 List 接口

/**
 * 分类控制器（用户端，面向普通用户）
 *
 * 负责处理茶产品分类相关的查询请求，包括：
 * - 获取所有分类列表
 * - 获取分类树形结构（用于前端展示多级分类菜单）
 *
 * 该控制器不需要用户登录即可访问，属于公开接口。
 *
 * 映射路径：/api/categories
 */
@RestController // 标识这是一个 RESTful 控制器，所有返回值自动序列化为 JSON
@RequestMapping("/api/categories") // 将控制器映射到 /api/categories 路径下
public class CategoryController {

    private final TeaCategoryService categoryService; // 茶分类业务服务，使用构造器注入

    /**
     * 构造器注入 TeaCategoryService
     *
     * @param categoryService 茶分类业务服务实例
     */
    public CategoryController(TeaCategoryService categoryService) { // 通过构造器注入依赖
        this.categoryService = categoryService; // 将注入的服务赋值给成员变量
    }

    /**
     * 获取所有分类
     *
     * 返回系统中所有的茶产品分类，按排序字段升序排列。
     * 分类信息包括分类 ID、名称、图标、排序权重等。
     *
     * 请求方式：GET /api/categories
     *
     * @return Result 包含所有分类列表的统一响应结果
     */
    @GetMapping // 处理 HTTP GET 请求，映射到 /api/categories
    public Result<List<CategoryVO>> getAll() { // 获取全部分类的方法
        return Result.success(categoryService.getAllCategories()); // 调用业务层获取全部分类并包装为成功响应
    }

    /**
     * 获取分类树形结构
     *
     * 返回分类的树形层级结构，用于前端构建多级菜单。
     * 每个父分类下包含其子分类列表，支持无限层级嵌套。
     *
     * 请求方式：GET /api/categories/tree
     *
     * @return Result 包含分类树形结构的统一响应结果
     */
    @GetMapping("/tree") // 处理 HTTP GET 请求，映射到 /api/categories/tree
    public Result<List<CategoryVO>> getTree() { // 获取分类树的方法
        return Result.success(categoryService.getCategoryTree()); // 调用业务层获取分类树并包装为成功响应
    }
}
