package com.yunming.tea.controller.admin;

import com.yunming.tea.common.Result;
import com.yunming.tea.entity.TeaCategory;
import com.yunming.tea.mapper.TeaCategoryMapper;
import com.yunming.tea.service.TeaCategoryService;
import com.yunming.tea.vo.CategoryVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {

    private final TeaCategoryService categoryService;
    private final TeaCategoryMapper categoryMapper;

    public AdminCategoryController(TeaCategoryService categoryService, TeaCategoryMapper categoryMapper) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
    }

    @GetMapping
    public Result<List<CategoryVO>> list() {
        return Result.success(categoryService.getAllCategories());
    }

    @GetMapping("/tree")
    public Result<List<CategoryVO>> tree() {
        return Result.success(categoryService.getCategoryTree());
    }

    @GetMapping("/{id}")
    public Result<TeaCategory> getById(@PathVariable Long id) {
        return Result.success(categoryService.getById(id));
    }

    @PostMapping
    public Result<TeaCategory> create(@RequestBody TeaCategory category) {
        categoryMapper.insert(category);
        return Result.success(category);
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody TeaCategory category) {
        category.setId(id);
        categoryMapper.updateById(category);
        return Result.success("更新成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        categoryMapper.deleteById(id);
        return Result.success("删除成功");
    }
}
