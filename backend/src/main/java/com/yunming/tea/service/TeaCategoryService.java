package com.yunming.tea.service;

import com.yunming.tea.entity.TeaCategory;
import com.yunming.tea.vo.CategoryVO;

import java.util.List;

/**
 * 茶品分类服务接口
 */
public interface TeaCategoryService {

    List<CategoryVO> getAllCategories();

    List<CategoryVO> getCategoryTree();

    TeaCategory getById(Long id);
}
