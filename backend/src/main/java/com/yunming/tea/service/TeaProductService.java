package com.yunming.tea.service; // 声明包路径，属于服务层接口包

import com.baomidou.mybatisplus.extension.plugins.pagination.Page; // 导入MyBatis-Plus分页对象，用于分页查询
import com.yunming.tea.dto.ProductSearchDTO; // 导入商品搜索数据传输对象，包含搜索条件和排序参数
import com.yunming.tea.entity.TeaProduct; // 导入茶品商品实体类，对应数据库中的商品表
import com.yunming.tea.vo.ProductDetailVO; // 导入商品详情视图对象，用于返回商品完整信息
import com.yunming.tea.vo.ProductListVO; // 导入商品列表视图对象，用于返回商品摘要信息

import java.util.List; // 导入List集合类，用于返回商品列表

/**
 * 商品服务接口
 * <p>
 * 该接口定义了茶品商品相关的业务操作规范，包括：
 * 1. 商品搜索（支持关键词、分类、标签筛选和多条件排序，分页）
 * 2. 获取商品详情（包含分类名称、商品图片和用户收藏状态）
 * 3. 获取热门商品列表
 * 4. 获取新品上市列表
 * 5. 获取推荐商品列表
 * 6. 根据ID获取商品实体
 * <p>
 * 商品服务是电商系统的核心，支撑首页展示、分类浏览、搜索等前端功能。
 * 所有查询仅返回上架状态（status=1）的商品数据。
 *
 * @author yunming
 * @see com.yunming.tea.service.impl.TeaProductServiceImpl
 */
public interface TeaProductService {

    /**
     * 搜索商品（综合查询，分页）
     * <p>
     * 支持多条件组合搜索商品，可按以下条件筛选：
     * - 关键词（keyword）：模糊匹配商品名称
     * - 分类ID（categoryId）：精确匹配商品分类
     * - 热门标签（isHot）：是否热门商品，0否1是
     * - 新品标签（isNew）：是否新品上市，0否1是
     * - 推荐标签（isRecommend）：是否推荐商品，0否1是
     * <p>
     * 支持以下排序方式：
     * - price_asc：价格从低到高
     * - price_desc：价格从高到低
     * - sales_desc：销量从高到低
     * - 默认：创建时间倒序（最新在前）
     * <p>
     * 只返回上架状态（status=1）的商品。
     *
     * @param dto 商品搜索数据传输对象，包含keyword、categoryId、isHot、isNew、isRecommend、sort、page、size等参数
     * @return 分页的商品列表视图对象，包含商品基本信息和分类名称
     */
    Page<ProductListVO> searchProducts(ProductSearchDTO dto); // 搜索商品方法声明

    /**
     * 获取商品详情
     * <p>
     * 根据商品ID查询商品的完整信息，包括：
     * - 商品基本信息（名称、描述、价格、库存等）
     * - 所属分类名称
     * - 商品多图列表
     * - 当前用户的收藏状态（如果传入了userId）
     *
     * @param productId 商品ID
     * @param userId 当前登录用户的ID，可为null（未登录时不查询收藏状态）
     * @return 商品详情视图对象，如果商品不存在则返回null
     */
    ProductDetailVO getProductDetail(Long productId, Long userId); // 获取商品详情方法声明

    /**
     * 获取热门商品列表
     * <p>
     * 查询标记为"热门"（isHot=1）且已上架的商品，按销量倒序排列，
     * 最多返回8条记录，用于首页热门推荐区域展示。
     *
     * @return 热门商品列表视图对象列表，最多8条
     */
    List<ProductListVO> getHotProducts(); // 获取热门商品方法声明

    /**
     * 获取新品上市列表
     * <p>
     * 查询标记为"新品"（isNew=1）且已上架的商品，按创建时间倒序排列，
     * 最多返回8条记录，用于首页新品展示区域。
     *
     * @return 新品商品列表视图对象列表，最多8条
     */
    List<ProductListVO> getNewProducts(); // 获取新品商品方法声明

    /**
     * 获取推荐商品列表
     * <p>
     * 查询标记为"推荐"（isRecommend=1）且已上架的商品，按创建时间倒序排列，
     * 最多返回8条记录，用于首页推荐展示区域。
     *
     * @return 推荐商品列表视图对象列表，最多8条
     */
    List<ProductListVO> getRecommendProducts(); // 获取推荐商品方法声明

    /**
     * 根据ID获取商品实体
     * <p>
     * 直接根据主键ID查询商品的完整实体信息，
     * 返回数据库中的原始记录，包含所有字段。
     * 不筛选上架状态，可用于订单中的商品查询。
     *
     * @param id 商品ID
     * @return 茶品商品实体对象，如果不存在则返回null
     */
    TeaProduct getById(Long id); // 根据ID获取商品方法声明
}
