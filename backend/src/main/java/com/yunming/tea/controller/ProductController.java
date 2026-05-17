package com.yunming.tea.controller; // 包声明：控制器层

import com.baomidou.mybatisplus.extension.plugins.pagination.Page; // MyBatis-Plus 分页结果对象，包含分页数据和总记录数
import com.yunming.tea.common.Result; // 统一响应结果封装类
import com.yunming.tea.dto.ProductSearchDTO; // 商品搜索/筛选请求的数据传输对象
import com.yunming.tea.service.TeaProductService; // 茶产品业务逻辑服务
import com.yunming.tea.vo.ProductDetailVO; // 商品详情视图对象
import com.yunming.tea.vo.ProductListVO; // 商品列表项视图对象
import org.springframework.web.bind.annotation.*; // Spring MVC 注解：包括 RestController、RequestMapping、GetMapping 等

import java.util.List; // Java 集合框架的 List 接口

/**
 * 商品控制器（用户端，面向普通用户）
 *
 * 负责处理茶产品相关的查询请求，是系统最核心的控制器之一，包括：
 * - 搜索与筛选商品（支持分页、分类、关键字搜索、价格区间等）
 * - 查看商品详情
 * - 获取热销商品列表
 * - 获取新品推荐列表
 * - 获取推荐商品列表
 *
 * 商品查询接口不需要登录即可访问，但在用户已登录时会额外返回收藏状态等信息。
 *
 * 映射路径：/api/products
 */
@RestController // 标识这是一个 RESTful 控制器，所有返回值自动序列化为 JSON
@RequestMapping("/api/products") // 将控制器映射到 /api/products 路径下
public class ProductController {

    private final TeaProductService productService; // 茶产品业务服务，使用构造器注入

    /**
     * 构造器注入 TeaProductService
     *
     * @param productService 茶产品业务服务实例
     */
    public ProductController(TeaProductService productService) { // 通过构造器注入依赖
        this.productService = productService; // 将注入的服务赋值给成员变量
    }

    /**
     * 搜索与筛选商品（分页）
     *
     * 支持多条件组合搜索：关键字模糊搜索、按分类筛选、按价格区间筛选、
     * 按销量/价格/评分排序等。所有筛选条件封装在 ProductSearchDTO 中。
     *
     * 请求方式：GET /api/products?keyword=龙井&categoryId=1&page=1&size=10
     *
     * @param dto 商品搜索数据传输对象，包含所有搜索和筛选条件（通过 GET 参数自动绑定）
     * @return Result 包含分页商品列表的统一响应结果
     */
    @GetMapping // 处理 HTTP GET 请求，映射到 /api/products
    public Result<Page<ProductListVO>> search(ProductSearchDTO dto) { // ProductSearchDTO 通过 GET 请求参数自动绑定
        return Result.success(productService.searchProducts(dto)); // 调用业务层搜索商品并包装为成功响应
    }

    /**
     * 查看商品详情
     *
     * 根据商品 ID 获取单个商品的完整信息，包括基本信息、详细描述、规格参数、
     * 商品图片、用户评价概览等。如果用户已登录，还会返回该用户是否收藏了该商品。
     *
     * 请求方式：GET /api/products/{id}
     *
     * @param id     商品 ID，从 URL 路径中获取
     * @param userId 当前登录用户的 ID，从请求属性中获取，可选（未登录时为 null）
     * @return Result 包含商品详细信息的统一响应结果；商品不存在时返回 404
     */
    @GetMapping("/{id}") // 处理 HTTP GET 请求，映射到 /api/products/{id}
    public Result<ProductDetailVO> detail(@PathVariable Long id, // @PathVariable 将 URL 中的 {id} 绑定到方法参数
                                           @RequestAttribute(value = "userId", required = false) Long userId) { // @RequestAttribute 从请求属性中获取 userId，可选参数
        ProductDetailVO vo = productService.getProductDetail(id, userId); // 调用业务层获取商品详情
        if (vo == null) { // 判断商品是否存在
            return Result.notFound("商品不存在"); // 商品不存在时返回 404 响应
        }
        return Result.success(vo); // 将商品详情视图对象包装为成功响应返回
    }

    /**
     * 获取热销商品列表
     *
     * 返回销量最高的前几个商品，用于首页"热销推荐"板块展示。
     * 热销排行根据商品的累计销售数量计算。
     *
     * 请求方式：GET /api/products/hot/list
     *
     * @return Result 包含热销商品列表的统一响应结果
     */
    @GetMapping("/hot/list") // 处理 HTTP GET 请求，映射到 /api/products/hot/list
    public Result<List<ProductListVO>> hotProducts() { // 获取热销商品列表的方法
        return Result.success(productService.getHotProducts()); // 调用业务层获取热销商品并包装为成功响应
    }

    /**
     * 获取新品推荐列表
     *
     * 返回最新上架的茶产品，用于首页"新品上市"板块展示。
     * 按商品的上架时间倒序排列。
     *
     * 请求方式：GET /api/products/new/list
     *
     * @return Result 包含新品推荐列表的统一响应结果
     */
    @GetMapping("/new/list") // 处理 HTTP GET 请求，映射到 /api/products/new/list
    public Result<List<ProductListVO>> newProducts() { // 获取新品推荐列表的方法
        return Result.success(productService.getNewProducts()); // 调用业务层获取新品商品并包装为成功响应
    }

    /**
     * 获取推荐商品列表
     *
     * 返回系统推荐的茶产品，用于首页"精选推荐"板块展示。
     * 推荐逻辑由业务层根据多个维度（评分、销量、新品等）综合计算得出。
     *
     * 请求方式：GET /api/products/recommend/list
     *
     * @return Result 包含推荐商品列表的统一响应结果
     */
    @GetMapping("/recommend/list") // 处理 HTTP GET 请求，映射到 /api/products/recommend/list
    public Result<List<ProductListVO>> recommendProducts() { // 获取推荐商品列表的方法
        return Result.success(productService.getRecommendProducts()); // 调用业务层获取推荐商品并包装为成功响应
    }
}
