package com.yunming.tea.service.impl; // 声明包路径，属于服务实现层包

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper; // 导入MyBatis-Plus的Lambda查询包装器
import com.yunming.tea.entity.ProductImage; // 导入商品图片实体类
import com.yunming.tea.mapper.ProductImageMapper; // 导入商品图片数据访问层
import com.yunming.tea.service.ProductImageService; // 导入商品图片服务接口
import org.springframework.stereotype.Service; // 导入Spring的Service注解

import java.util.List; // 导入List接口

/**
 * 商品图片服务实现类
 * <p>
 * 实现了{@link ProductImageService}接口中定义的商品图片查询业务逻辑。
 * 主要功能包括：
 * <ul>
 *   <li>根据商品ID查询该商品的所有图片列表（按排序字段升序）</li>
 * </ul>
 * <p>
 * 每个商品可以关联多张展示图片（通常是1张主图+多张详情附图），
 * 通过sort字段控制图片在前端轮播或列表中的展示顺序。
 * 图片URL通常由文件上传服务生成并存储。
 *
 * @author yunming
 * @see ProductImageService
 */
@Service // 将该类标记为Spring的Service组件
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageMapper imageMapper; // 商品图片数据访问对象

    /**
     * 构造函数注入依赖
     *
     * @param imageMapper 商品图片数据访问对象
     */
    public ProductImageServiceImpl(ProductImageMapper imageMapper) {
        this.imageMapper = imageMapper; // 注入商品图片Mapper
    }

    /**
     * 根据商品ID获取商品的所有图片列表
     * <p>
     * 查询指定商品的所有图片记录，按排序字段升序排列。
     * 排序确保主图或重要的图片展示在前面，保持前端显示的一致顺序。
     *
     * @param productId 商品ID
     * @return 商品图片实体对象列表，按sort字段升序排列
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public List<ProductImage> getByProductId(Long productId) {
        // 构建查询条件
        LambdaQueryWrapper<ProductImage> wrapper = new LambdaQueryWrapper<>(); // 创建查询包装器
        wrapper.eq(ProductImage::getProductId, productId) // 条件：商品ID精确匹配
                .orderByAsc(ProductImage::getSort); // 排序：按排序字段升序（sort值小的排在前面）
        return imageMapper.selectList(wrapper); // 执行查询并返回图片列表
    }
}
