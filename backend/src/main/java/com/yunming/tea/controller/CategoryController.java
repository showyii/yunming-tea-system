package com.yunming.tea.controller;

import com.yunming.tea.common.Result;
import com.yunming.tea.service.TeaCategoryService;
import com.yunming.tea.vo.CategoryVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类控制器（用户端）
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final TeaCategoryService categoryService;

    public CategoryController(TeaCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * 获取所有分类
     * GET /api/categories
     */
    @GetMapping
    public Result<List<CategoryVO>> getAll() {
        return Result.success(categoryService.getAllCategories());
    }

    /**
     * 获取分类树
     * GET /api/categories/tree
     */
    @GetMapping("/tree")
    public Result<List<CategoryVO>> getTree() {
        return Result.success(categoryService.getCategoryTree());
    }
}
