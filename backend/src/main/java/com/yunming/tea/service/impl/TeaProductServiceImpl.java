package com.yunming.tea.service.impl; // 声明包路径，属于服务实现层包

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper; // 导入MyBatis-Plus的Lambda查询包装器
import com.baomidou.mybatisplus.extension.plugins.pagination.Page; // 导入MyBatis-Plus分页对象
import com.yunming.tea.dto.ProductSearchDTO; // 导入商品搜索数据传输对象
import com.yunming.tea.entity.ProductImage; // 导入商品图片实体类
import com.yunming.tea.entity.TeaCategory; // 导入茶品分类实体类
import com.yunming.tea.entity.TeaProduct; // 导入茶品商品实体类
import com.yunming.tea.mapper.TeaCategoryMapper; // 导入茶品分类数据访问层
import com.yunming.tea.mapper.TeaProductMapper; // 导入茶品商品数据访问层
import com.yunming.tea.service.FavoriteService; // 导入收藏服务，用于查询用户收藏状态
import com.yunming.tea.service.ProductImageService; // 导入商品图片服务，用于获取商品多图
import com.yunming.tea.service.TeaProductService; // 导入商品服务接口
import com.yunming.tea.vo.ProductDetailVO; // 导入商品详情视图对象
import com.yunming.tea.vo.ProductListVO; // 导入商品列表视图对象
import org.springframework.stereotype.Service; // 导入Spring的Service注解

import java.util.List; // 导入List接口
import java.util.stream.Collectors; // 导入Collectors工具类

/**
 * 商品服务实现类
 * <p>
 * 实现了{@link TeaProductService}接口中定义的茶品商品相关业务逻辑。
 * 主要功能包括：
 * <ul>
 *   <li>商品搜索：支持关键词模糊搜索、分类筛选、标签筛选和多条件排序，结果分页</li>
 *   <li>商品详情：获取商品完整信息，包括分类名称、多图和用户收藏状态</li>
 *   <li>热门商品：查询标记为热门且已上架的商品，按销量降序取前8条</li>
 *   <li>新品上市：查询标记为新品且已上架的商品，按创建时间降序取前8条</li>
 *   <li>推荐商品：查询标记为推荐且已上架的商品，按创建时间降序取前8条</li>
 *   <li>根据ID获取商品实体：直接查询数据库原始记录</li>
 * </ul>
 * <p>
 * 依赖的其他服务：
 * <ul>
 *   <li>{@link ProductImageService}：获取商品的多张展示图片</li>
 *   <li>{@link FavoriteService}：判断当前用户是否已收藏该商品</li>
 * </ul>
 *
 * @author yunming
 * @see TeaProductService
 */
@Service // 将该类标记为Spring的Service组件
public class TeaProductServiceImpl implements TeaProductService {

    private final TeaProductMapper productMapper; // 茶品商品数据访问对象
    private final TeaCategoryMapper categoryMapper; // 茶品分类数据访问对象，用于获取分类名称
    private final ProductImageService imageService; // 商品图片服务，用于获取商品多图
    private final FavoriteService favoriteService; // 收藏服务，用于判断用户是否收藏

    /**
     * 构造函数注入依赖
     *
     * @param productMapper 商品数据访问对象
     * @param categoryMapper 分类数据访问对象
     * @param imageService 商品图片服务
     * @param favoriteService 收藏服务
     */
    public TeaProductServiceImpl(TeaProductMapper productMapper, TeaCategoryMapper categoryMapper,
                                  ProductImageService imageService, FavoriteService favoriteService) {
        this.productMapper = productMapper; // 注入商品Mapper
        this.categoryMapper = categoryMapper; // 注入分类Mapper
        this.imageService = imageService; // 注入图片服务
        this.favoriteService = favoriteService; // 注入收藏服务
    }

    /**
     * 搜索商品（综合查询，分页）
     * <p>
     * 支持多条件组合搜索和多种排序方式。
     * 条件筛选（全部可选，可自由组合）：
     * <ul>
     *   <li>keyword：关键词，对商品名称进行模糊匹配</li>
     *   <li>categoryId：分类ID，精确匹配</li>
     *   <li>isHot：是否热门，0否1是</li>
     *   <li>isNew：是否新品，0否1是</li>
     *   <li>isRecommend：是否推荐，0否1是</li>
     * </ul>
     * 排序方式：
     * <ul>
     *   <li>price_asc：价格升序</li>
     *   <li>price_desc：价格降序</li>
     *   <li>sales_desc：销量降序</li>
     *   <li>默认：创建时间降序（最新在前）</li>
     * </ul>
     * 只返回上架状态（status=1）的商品。
     *
     * @param dto 商品搜索数据传输对象
     * @return 分页的商品列表视图对象
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public Page<ProductListVO> searchProducts(ProductSearchDTO dto) {
        // 第1步：构建动态查询条件
        LambdaQueryWrapper<TeaProduct> wrapper = new LambdaQueryWrapper<>(); // 创建查询包装器
        wrapper.eq(TeaProduct::getStatus, 1); // 基础条件：只查询已上架的商品（status=1）

        // 关键词搜索：对商品名称进行模糊匹配
        if (dto.getKeyword() != null && !dto.getKeyword().isBlank()) { // 关键词不为null且不为空白字符串
            wrapper.like(TeaProduct::getName, dto.getKeyword()); // 条件：名称包含关键词（LIKE '%keyword%'）
        }
        // 分类筛选：精确匹配分类ID
        if (dto.getCategoryId() != null) { // 分类ID不为null
            wrapper.eq(TeaProduct::getCategoryId, dto.getCategoryId()); // 条件：分类ID精确匹配
        }
        // 热门标签筛选
        if (dto.getIsHot() != null) { // isHot参数不为null
            wrapper.eq(TeaProduct::getIsHot, dto.getIsHot()); // 条件：热门标记匹配
        }
        // 新品标签筛选
        if (dto.getIsNew() != null) { // isNew参数不为null
            wrapper.eq(TeaProduct::getIsNew, dto.getIsNew()); // 条件：新品标记匹配
        }
        // 推荐标签筛选
        if (dto.getIsRecommend() != null) { // isRecommend参数不为null
            wrapper.eq(TeaProduct::getIsRecommend, dto.getIsRecommend()); // 条件：推荐标记匹配
        }

        // 第2步：设置排序方式
        if ("price_asc".equals(dto.getSort())) { // 价格升序
            wrapper.orderByAsc(TeaProduct::getPrice); // 按价格升序排列
        } else if ("price_desc".equals(dto.getSort())) { // 价格降序
            wrapper.orderByDesc(TeaProduct::getPrice); // 按价格降序排列
        } else if ("sales_desc".equals(dto.getSort())) { // 销量降序
            wrapper.orderByDesc(TeaProduct::getSales); // 按销量降序排列
        } else { // 默认排序
            wrapper.orderByDesc(TeaProduct::getCreateTime); // 按创建时间降序（最新商品在前）
        }

        // 第3步：执行分页查询
        Page<TeaProduct> page = new Page<>(dto.getPage(), dto.getSize()); // 创建分页对象
        Page<TeaProduct> result = productMapper.selectPage(page, wrapper); // 执行分页查询

        // 第4步：将商品实体列表映射为视图对象列表
        List<ProductListVO> records = result.getRecords().stream() // 获取当前页记录流
                .map(this::toListVO) // 将每个实体映射为列表视图对象
                .collect(Collectors.toList()); // 收集为List

        // 第5步：构建分页视图对象并返回
        Page<ProductListVO> voPage = new Page<>(); // 创建分页视图对象
        voPage.setRecords(records); // 设置记录列表
        voPage.setTotal(result.getTotal()); // 设置总记录数
        voPage.setCurrent(result.getCurrent()); // 设置当前页码
        voPage.setSize(result.getSize()); // 设置每页大小
        voPage.setPages(result.getPages()); // 设置总页数
        return voPage; // 返回分页视图对象
    }

    /**
     * 获取商品详情
     * <p>
     * 根据商品ID查询商品的完整信息，并附加：
     * <ul>
     *   <li>所属分类的名称</li>
     *   <li>商品的所有展示图片列表</li>
     *   <li>当前用户的收藏状态（如果传入了userId）</li>
     * </ul>
     *
     * @param productId 商品ID
     * @param userId 当前用户ID（可选，用于判断收藏状态）
     * @return 商品详情视图对象，如果商品不存在则返回null
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public ProductDetailVO getProductDetail(Long productId, Long userId) {
        // 第1步：查询商品基本信息
        TeaProduct product = productMapper.selectById(productId); // 根据ID查询商品
        if (product == null) return null; // 如果商品不存在，返回null

        // 第2步：将商品实体转换为详情视图对象
        ProductDetailVO vo = toDetailVO(product); // 转换为包含基本字段的视图对象

        // 第3步：查询并设置分类名称
        TeaCategory category = categoryMapper.selectById(product.getCategoryId()); // 根据分类ID查询分类
        if (category != null) vo.setCategoryName(category.getName()); // 设置分类名称

        // 第4步：查询并设置商品多图
        List<ProductImage> images = imageService.getByProductId(productId); // 调用图片服务获取商品所有图片
        vo.setImages(images.stream() // 获取图片列表流
                .map(ProductImage::getImageUrl) // 提取每张图片的URL
                .collect(Collectors.toList())); // 收集为图片URL列表

        // 第5步：如果用户已登录，查询收藏状态
        if (userId != null) { // 用户ID不为null（用户已登录）
            vo.setIsFavorited(favoriteService.isFavorited(userId, productId)); // 查询并设置是否已收藏
        }

        return vo; // 返回商品详情视图对象
    }

    /**
     * 获取热门商品列表
     * <p>
     * 查询标记为"热门"（isHot=1）且已上架的商品，按销量降序排列，最多返回8条。
     * 用于首页"热门推荐"区域的展示。
     *
     * @return 热门商品列表视图对象列表，最多8条
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public List<ProductListVO> getHotProducts() {
        // 构建查询条件
        LambdaQueryWrapper<TeaProduct> wrapper = new LambdaQueryWrapper<>(); // 创建查询包装器
        wrapper.eq(TeaProduct::getStatus, 1) // 条件：已上架（status=1）
                .eq(TeaProduct::getIsHot, 1) // 条件：标记为热门（isHot=1）
                .orderByDesc(TeaProduct::getSales) // 排序：按销量降序（销量高的在前）
                .last("LIMIT 8"); // 限制返回条数：最多8条（使用MySQL的LIMIT子句）
        // 查询并映射为视图对象列表
        return productMapper.selectList(wrapper).stream() // 执行查询获取流
                .map(this::toListVO) // 映射为列表视图对象
                .collect(Collectors.toList()); // 收集为List
    }

    /**
     * 获取新品上市列表
     * <p>
     * 查询标记为"新品"（isNew=1）且已上架的商品，按创建时间降序排列，最多返回8条。
     * 用于首页"新品上市"区域的展示。
     *
     * @return 新品商品列表视图对象列表，最多8条
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public List<ProductListVO> getNewProducts() {
        // 构建查询条件
        LambdaQueryWrapper<TeaProduct> wrapper = new LambdaQueryWrapper<>(); // 创建查询包装器
        wrapper.eq(TeaProduct::getStatus, 1) // 条件：已上架
                .eq(TeaProduct::getIsNew, 1) // 条件：标记为新品（isNew=1）
                .orderByDesc(TeaProduct::getCreateTime) // 排序：按创建时间降序（最新发布的在前）
                .last("LIMIT 8"); // 限制最多8条
        // 查询并映射
        return productMapper.selectList(wrapper).stream() // 执行查询
                .map(this::toListVO) // 映射为视图对象
                .collect(Collectors.toList()); // 收集为List
    }

    /**
     * 获取推荐商品列表
     * <p>
     * 查询标记为"推荐"（isRecommend=1）且已上架的商品，按创建时间降序排列，最多返回8条。
     * 用于首页"为您推荐"区域的展示。
     *
     * @return 推荐商品列表视图对象列表，最多8条
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public List<ProductListVO> getRecommendProducts() {
        // 构建查询条件
        LambdaQueryWrapper<TeaProduct> wrapper = new LambdaQueryWrapper<>(); // 创建查询包装器
        wrapper.eq(TeaProduct::getStatus, 1) // 条件：已上架
                .eq(TeaProduct::getIsRecommend, 1) // 条件：标记为推荐（isRecommend=1）
                .orderByDesc(TeaProduct::getCreateTime) // 排序：按创建时间降序
                .last("LIMIT 8"); // 限制最多8条
        // 查询并映射
        return productMapper.selectList(wrapper).stream() // 执行查询
                .map(this::toListVO) // 映射为视图对象
                .collect(Collectors.toList()); // 收集为List
    }

    /**
     * 根据ID获取商品实体
     * <p>
     * 直接根据主键ID查询商品的完整实体信息，不限制上架状态。
     * 可用于订单等需要查询任意状态商品的场景。
     *
     * @param id 商品ID
     * @return 茶品商品实体对象，如果不存在则返回null
     */
    @Override // 标识该方法覆盖了父接口中的方法
    public TeaProduct getById(Long id) {
        return productMapper.selectById(id); // 根据主键ID查询，返回实体或null
    }

    /**
     * 将商品实体转换为列表视图对象
     * <p>
     * 列表视图包含商品的基本展示信息和所属分类名称，
     * 用于搜索结果和首页推荐区域的商品卡片展示。
     *
     * @param p 商品实体对象
     * @return 商品列表视图对象
     */
    private ProductListVO toListVO(TeaProduct p) { // 私有方法，实体转列表视图
        ProductListVO vo = new ProductListVO(); // 创建列表视图对象
        vo.setId(p.getId()); // 设置商品ID
        vo.setCategoryId(p.getCategoryId()); // 设置分类ID
        vo.setName(p.getName()); // 设置商品名称
        vo.setSubtitle(p.getSubtitle()); // 设置商品副标题/简介
        vo.setPrice(p.getPrice()); // 设置商品现价
        vo.setOriginalPrice(p.getOriginalPrice()); // 设置商品原价（用于展示划线价）
        vo.setSales(p.getSales()); // 设置商品销量
        vo.setMainImage(p.getMainImage()); // 设置商品主图
        vo.setIsHot(p.getIsHot()); // 设置是否热门标记
        vo.setIsNew(p.getIsNew()); // 设置是否新品标记
        vo.setIsRecommend(p.getIsRecommend()); // 设置是否推荐标记
        // 查询并设置分类名称（用于商品列表的分类标签展示）
        TeaCategory cat = categoryMapper.selectById(p.getCategoryId()); // 根据分类ID查询分类
        if (cat != null) vo.setCategoryName(cat.getName()); // 如果分类存在，设置分类名称
        return vo; // 返回列表视图对象
    }

    /**
     * 将商品实体转换为详情视图对象
     * <p>
     * 详情视图包含商品的完整信息（比列表视图多出描述和库存等字段），
     * 用于商品详情页面的展示。
     *
     * @param p 商品实体对象
     * @return 商品详情视图对象
     */
    private ProductDetailVO toDetailVO(TeaProduct p) { // 私有方法，实体转详情视图
        ProductDetailVO vo = new ProductDetailVO(); // 创建详情视图对象
        vo.setId(p.getId()); // 设置商品ID
        vo.setCategoryId(p.getCategoryId()); // 设置分类ID
        vo.setName(p.getName()); // 设置商品名称
        vo.setSubtitle(p.getSubtitle()); // 设置商品副标题
        vo.setDescription(p.getDescription()); // 设置商品详细描述（支持HTML富文本）
        vo.setPrice(p.getPrice()); // 设置商品现价
        vo.setOriginalPrice(p.getOriginalPrice()); // 设置商品原价
        vo.setStock(p.getStock()); // 设置当前库存数量
        vo.setSales(p.getSales()); // 设置历史销量
        vo.setMainImage(p.getMainImage()); // 设置商品主图
        vo.setIsHot(p.getIsHot()); // 设置是否热门
        vo.setIsNew(p.getIsNew()); // 设置是否新品
        vo.setIsRecommend(p.getIsRecommend()); // 设置是否推荐
        vo.setStatus(p.getStatus()); // 设置商品状态（1上架/0下架）
        vo.setCreateTime(p.getCreateTime()); // 设置商品创建时间
        return vo; // 返回详情视图对象
    }
}
