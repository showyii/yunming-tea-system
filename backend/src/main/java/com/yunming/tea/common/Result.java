// 声明当前类所属的包路径
package com.yunming.tea.common;

// Lombok 注解：为所有字段生成 getter/setter/equals/hashCode/toString 方法
import lombok.AllArgsConstructor;
// Lombok 注解：生成包含所有字段的全参构造方法
import lombok.Data;
// Lombok 注解：生成无参构造方法
import lombok.NoArgsConstructor;

/**
 * 统一响应结果封装类
 * 将后端接口的返回数据统一包装成 {code, message, data} 格式
 * code: 状态码（200成功，400参数错误，401未授权，403禁止，404未找到，500服务器错误）
 * message: 提示信息
 * data: 返回的数据，泛型 T 支持任意类型
 */
@Data // 自动生成 getter、setter、toString 等方法
@NoArgsConstructor // 自动生成无参构造函数
@AllArgsConstructor // 自动生成全参构造函数
public class Result<T> { // 泛型类 T 表示返回数据的类型

    private Integer code; // HTTP 状态码，200 表示成功
    private String message; // 返回给前端的提示消息
    private T data; // 返回给前端的数据体，可以是任意类型

    /**
     * 成功响应（带数据）
     * @param data 要返回的数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data); // 创建 code=200 的成功响应
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "success", null); // data 设为 null
    }

    /**
     * 成功响应（自定义消息+数据）
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data); // 使用自定义消息
    }

    /**
     * 通用错误响应
     * @param code 自定义错误状态码
     * @param message 错误提示信息
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null); // 返回指定错误码和消息
    }

    /**
     * 默认服务器错误响应（code=500）
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null); // 默认 500 服务器内部错误
    }

    /**
     * 请求参数错误响应（code=400）
     */
    public static <T> Result<T> badRequest(String message) {
        return new Result<>(400, message, null); // 400 Bad Request
    }

    /**
     * 未授权响应（code=401），用于未登录或登录过期
     */
    public static <T> Result<T> unauthorized(String message) {
        return new Result<>(401, message, null); // 401 Unauthorized
    }

    /**
     * 禁止访问响应（code=403），用于权限不足
     */
    public static <T> Result<T> forbidden(String message) {
        return new Result<>(403, message, null); // 403 Forbidden
    }

    /**
     * 资源未找到响应（code=404）
     */
    public static <T> Result<T> notFound(String message) {
        return new Result<>(404, message, null); // 404 Not Found
    }
}
