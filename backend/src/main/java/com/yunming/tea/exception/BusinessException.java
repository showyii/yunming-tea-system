// 声明包路径
package com.yunming.tea.exception;

// Lombok 注解：为 code 字段生成 getter 方法
import lombok.Getter;

/**
 * 业务异常类
 * 用于在业务逻辑中抛出可预期的异常，配合全局异常处理器返回友好的错误信息给前端
 * 继承自 RuntimeException（运行时异常），不需要在方法签名中声明 throws
 */
@Getter // 自动生成 getCode() 方法
public class BusinessException extends RuntimeException { // 继承运行时异常

    // 异常对应的错误码，如 400（参数错误）、401（未登录）等
    private final Integer code;

    /**
     * 构造方法（带错误码和错误消息）
     * @param code HTTP 状态码
     * @param message 错误提示信息
     */
    public BusinessException(Integer code, String message) {
        super(message); // 调用父类 RuntimeException 的构造器，设置异常消息
        this.code = code; // 保存错误码
    }

    /**
     * 构造方法（仅带错误消息，默认错误码 500）
     * @param message 错误提示信息
     */
    public BusinessException(String message) {
        super(message); // 调用父类构造器设置消息
        this.code = 500; // 默认服务器内部错误
    }
}
