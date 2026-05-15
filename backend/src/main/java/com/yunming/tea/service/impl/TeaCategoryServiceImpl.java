package com.yunming.tea.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yunming.tea.entity.TeaCategory;
import com.yunming.tea.mapper.TeaCategoryMapper;
import com.yunming.tea.service.TeaCategoryService;
import com.yunming.tea.vo.CategoryVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TeaCategoryServiceImpl implements TeaCategoryService {

    private final TeaCategoryMapper categoryMapper;

    public TeaCategoryServiceImpl(TeaCategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<CategoryVO> getAllCategories() {
        LambdaQueryWrapper<TeaCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.isNull(TeaCategory::getDescription).or().notLike(TeaCategory::getDescription, "auto-test"));
        wrapper.notLike(TeaCategory::getName, "test_");
        wrapper.ne(TeaCategory::getName, "测试分类");
        wrapper.orderByAsc(TeaCategory::getSort);
        List<TeaCategory> categories = categoryMapper.selectList(wrapper);
        return categories.stream().map(this::toVO).collect(Collectors.toList());
    }

    @Override
    public List<CategoryVO> getCategoryTree() {
        List<TeaCategory> all = categoryMapper.selectList(
                new LambdaQueryWrapper<TeaCategory>()
                        .and(w -> w.isNull(TeaCategory::getDescription).or().notLike(TeaCategory::getDescription, "auto-test"))
                        .notLike(TeaCategory::getName, "test_")
                        .ne(TeaCategory::getName, "测试分类")
                        .orderByAsc(TeaCategory::getSort));

        Map<Long, List<TeaCategory>> parentMap = all.stream()
                .filter(c -> c.getParentId() != null && c.getParentId() != 0)
                .collect(Collectors.groupingBy(TeaCategory::getParentId));

        List<CategoryVO> roots = new ArrayList<>();
        for (TeaCategory c : all) {
            if (c.getParentId() == null || c.getParentId() == 0) {
                CategoryVO vo = toVO(c);
                vo.setChildren(buildChildren(c.getId(), parentMap));
                roots.add(vo);
            }
        }
        return roots;
    }

    private List<CategoryVO> buildChildren(Long parentId, Map<Long, List<TeaCategory>> parentMap) {
        List<TeaCategory> children = parentMap.get(parentId);
        if (children == null) return null;
        return children.stream().map(c -> {
            CategoryVO vo = toVO(c);
            vo.setChildren(buildChildren(c.getId(), parentMap));
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public TeaCategory getById(Long id) {
        return categoryMapper.selectById(id);
    }

    private CategoryVO toVO(TeaCategory c) {
        CategoryVO vo = new CategoryVO();
        vo.setId(c.getId());
        vo.setName(c.getName());
        vo.setParentId(c.getParentId());
        vo.setSort(c.getSort());
        vo.setIcon(c.getIcon());
        vo.setDescription(c.getDescription());
        vo.setCreateTime(c.getCreateTime());
        return vo;
    }
}
