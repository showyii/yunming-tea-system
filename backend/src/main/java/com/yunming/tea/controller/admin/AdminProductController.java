package com.yunming.tea.controller.admin; // 包声明：管理后台控制器层

import com.baomidou.mybatisplus.extension.plugins.pagination.Page; // MyBatis-Plus 分页结果对象，包含分页数据和总记录数
import com.yunming.tea.common.Result; // 统一响应结果封装类
import com.yunming.tea.dto.ProductSearchDTO; // 商品搜索/筛选请求的数据传输对象
import com.yunming.tea.entity.ProductImage; // 商品图片实体类
import com.yunming.tea.entity.TeaProduct; // 茶产品实体类
import com.yunming.tea.mapper.ProductImageMapper; // 商品图片数据访问层（MyBatis-Plus Mapper）
import com.yunming.tea.mapper.TeaProductMapper; // 茶产品数据访问层（MyBatis-Plus Mapper）
import com.yunming.tea.service.ProductImageService; // 商品图片业务逻辑服务
import com.yunming.tea.service.TeaProductService; // 茶产品业务逻辑服务
import com.yunming.tea.vo.ProductDetailVO; // 商品详情视图对象
import com.yunming.tea.vo.ProductListVO; // 商品列表项视图对象
import org.springframework.web.bind.annotation.*; // Spring MVC 注解：包括 RestController、RequestMapping、GetMapping 等

import java.util.List; // Java 集合框架的 List 接口

/**
 * 管理员 - 商品管理控制器
 *
 * 负责管理后台对茶产品的全面管理功能，包括：
 * - 搜索和查看所有商品（分页）
 * - 查看商品详情
 * - 创建新商品
 * - 编辑商品信息
 * - 删除商品
 * - 管理商品图片（查看、添加、删除）
 *
 * 这是管理后台最核心的控制器之一，涉及商品和图片的完整 CRUD 操作。
 *
 * 映射路径：/api/admin/products
 */
@RestController // 标识这是一个 RESTful 控制器，所有返回值自动序列化为 JSON
@RequestMapping("/api/admin/products") // 将控制器映射到 /api/admin/products 路径下
public class AdminProductController {

    private final TeaProductService productService; // 茶产品业务服务，用于查询操作（搜索和详情）
    private final TeaProductMapper productMapper; // 茶产品 Mapper，用于直接的增删改操作
    private final ProductImageService imageService; // 商品图片业务服务，用于查询商品图片
    private final ProductImageMapper imageMapper; // 商品图片 Mapper，用于直接的图片增删操作

    /**
     * 构造器注入多个依赖：ProductService、ProductMapper、ImageService、ImageMapper
     *
     * @param productService 茶产品业务服务实例
     * @param productMapper  茶产品数据访问层实例
     * @param imageService   商品图片业务服务实例
     * @param imageMapper    商品图片数据访问层实例
     */
    public AdminProductController(TeaProductService productService, TeaProductMapper productMapper, // 构造器参数：产品和图片相关的服务和 Mapper
                                  ProductImageService imageService, ProductImageMapper imageMapper) { // 第二行参数：图片服务和图片 Mapper
        this.productService = productService; // 将注入的产品服务赋值给成员变量
        this.productMapper = productMapper; // 将注入的产品 Mapper 赋值给成员变量
        this.imageService = imageService; // 将注入的图片服务赋值给成员变量
        this.imageMapper = imageMapper; // 将注入的图片 Mapper 赋值给成员变量
    }

    /**
     * 搜索与筛选商品（管理员视图，分页）
     *
     * 管理员搜索和查看系统中的所有商品，支持多条件筛选。
     * 可以看到所有状态（上架、下架）的商品。
     *
     * 请求方式：GET /api/admin/products?keyword=龙井&categoryId=1&page=1&size=10
     *
     * @param dto 商品搜索数据传输对象，包含所有搜索和筛选条件
     * @return Result 包含分页商品列表的统一响应结果
     */
    @GetMapping // 处理 HTTP GET 请求，映射到 /api/admin/products
    public Result<Page<ProductListVO>> list(ProductSearchDTO dto) { // ProductSearchDTO 通过 GET 请求参数自动绑定
        return Result.success(productService.searchProducts(dto)); // 调用业务层搜索商品并包装为成功响应
    }

    /**
     * 查看商品详情（管理员视图）
     *
     * 获取指定商品的完整信息，包括基本信息、详细描述、规格参数、
     * 商品图片、库存信息等。
     *
     * 请求方式：GET /api/admin/products/{id}
     *
     * @param id 商品 ID，从 URL 路径中获取
     * @return Result 包含商品详细信息的统一响应结果
     */
    @GetMapping("/{id}") // 处理 HTTP GET 请求，映射到 /api/admin/products/{id}
    public Result<ProductDetailVO> detail(@PathVariable Long id) { // @PathVariable 将 URL 中的 {id} 绑定到方法参数
        return Result.success(productService.getProductDetail(id, null)); // 调用业务层获取商品详情（传 null 表示不按用户筛选），包装为成功响应
    }

    /**
     * 创建新商品
     *
     * 在系统中添加一个新的茶产品。接收商品的完整信息（名称、分类、
     * 价格、库存、描述、图片等），保存到数据库。
     *
     * 请求方式：POST /api/admin/products
     *
     * @param product 茶产品实体对象，由请求体 JSON 自动反序列化
     * @return Result 包含新创建的商品实体（含自动生成的 ID）的统一响应结果
     */
    @PostMapping // 处理 HTTP POST 请求，映射到 /api/admin/products
    public Result<TeaProduct> create(@RequestBody TeaProduct product) { // @RequestBody 将请求体 JSON 反序列化为 TeaProduct 实体
        productMapper.insert(product); // 使用 Mapper 直接将商品实体插入数据库
        return Result.success(product); // 返回新创建的商品实体（此时 ID 已由数据库自动填充）
    }

    /**
     * 编辑商品信息
     *
     * 更新指定商品的信息，包括名称、价格、库存、描述、分类等。
     *
     * 请求方式：PUT /api/admin/products/{id}
     *
     * @param id      商品 ID，从 URL 路径中获取，标识要修改的商品
     * @param product 茶产品实体对象，包含要更新的字段，由请求体 JSON 自动反序列化
     * @return Result 包含更新成功提示的统一响应结果
     */
    @PutMapping("/{id}") // 处理 HTTP PUT 请求，映射到 /api/admin/products/{id}
    public Result<String> update(@PathVariable Long id, @RequestBody TeaProduct product) { // @PathVariable 从 URL 获取 ID，@RequestBody 反序列化请求体
        product.setId(id); // 将路径参数中的 ID 设置到商品实体上，确保更新正确的记录
        productMapper.updateById(product); // 使用 Mapper 根据 ID 更新商品记录
        return Result.success("更新成功"); // 返回更新成功的提示信息
    }

    /**
     * 删除商品
     *
     * 根据商品 ID 从数据库中删除指定的商品。删除前应注意：
     * - 该商品是否有关联的订单
     * - 该商品是否在用户购物车或收藏中
     * 建议先下架商品，确认无影响后再执行删除。
     *
     * 请求方式：DELETE /api/admin/products/{id}
     *
     * @param id 商品 ID，从 URL 路径中获取，标识要删除的商品
     * @return Result 包含删除成功提示的统一响应结果
     */
    @DeleteMapping("/{id}") // 处理 HTTP DELETE 请求，映射到 /api/admin/products/{id}
    public Result<String> delete(@PathVariable Long id) { // @PathVariable 将 URL 中的 {id} 绑定到方法参数
        productMapper.deleteById(id); // 使用 Mapper 根据 ID 删除商品记录
        return Result.success("删除成功"); // 返回删除成功的提示信息
    }

    /**
     * 获取商品的所有图片
     *
     * 查询指定商品关联的所有图片记录，包括主图和详情图。
     *
     * 请求方式：GET /api/admin/products/{productId}/images
     *
     * @param productId 商品 ID，从 URL 路径中获取
     * @return Result 包含商品图片列表的统一响应结果
     */
    @GetMapping("/{productId}/images") // 处理 HTTP GET 请求，映射到 /api/admin/products/{productId}/images
    public Result<List<ProductImage>> images(@PathVariable Long productId) { // @PathVariable 将 URL 中的 {productId} 绑定到方法参数
        return Result.success(imageService.getByProductId(productId)); // 调用图片服务获取该商品的所有图片并包装为成功响应
    }

    /**
     * 为商品添加图片
     *
     * 给指定商品添加一张新图片。图片信息包含图片 URL、排序顺序、是否为主图等。
     *
     * 请求方式：POST /api/admin/products/{productId}/images
     *
     * @param productId 商品 ID，从 URL 路径中获取，指定图片所属的商品
     * @param image     商品图片实体对象，由请求体 JSON 自动反序列化
     * @return Result 包含添加成功提示的统一响应结果
     */
    @PostMapping("/{productId}/images") // 处理 HTTP POST 请求，映射到 /api/admin/products/{productId}/images
    public Result<String> addImage(@PathVariable Long productId, @RequestBody ProductImage image) { // @PathVariable 获取商品 ID，@RequestBody 反序列化图片实体
        image.setProductId(productId); // 将路径参数中的商品 ID 设置到图片实体的关联字段上
        imageMapper.insert(image); // 使用 Mapper 将图片记录插入数据库
        return Result.success("添加成功"); // 返回添加成功的提示信息
    }

    /**
     * 删除商品图片
     *
     * 根据图片 ID 删除指定的商品图片记录。
     *
     * 请求方式：DELETE /api/admin/products/images/{imageId}
     *
     * @param imageId 图片 ID，从 URL 路径中获取，标识要删除的图片
     * @return Result 包含删除成功提示的统一响应结果
     */
    @DeleteMapping("/images/{imageId}") // 处理 HTTP DELETE 请求，映射到 /api/admin/products/images/{imageId}
    public Result<String> deleteImage(@PathVariable Long imageId) { // @PathVariable 将 URL 中的 {imageId} 绑定到方法参数
        imageMapper.deleteById(imageId); // 使用 Mapper 根据 ID 删除图片记录
        return Result.success("删除成功"); // 返回删除成功的提示信息
    }
}
