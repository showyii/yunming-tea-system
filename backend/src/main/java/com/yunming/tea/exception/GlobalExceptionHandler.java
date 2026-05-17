// 声明包路径
package com.yunming.tea.exception;

// 导入统一响应结果类
import com.yunming.tea.common.Result;
// Lombok 日志注解，自动生成 log 对象
import lombok.extern.slf4j.Slf4j;
// Spring HTTP 状态枚举
import org.springframework.http.HttpStatus;
// 导入参数绑定异常
import org.springframework.validation.BindException;
// 异常处理器注解
import org.springframework.web.bind.annotation.ExceptionHandler;
// HTTP 响应状态注解
import org.springframework.web.bind.annotation.ResponseStatus;
// 全局控制器增强注解
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 使用 @RestControllerAdvice 拦截所有 Controller 抛出的异常
 * 将不同类型的异常转换为统一的 Result 响应格式
 */
@Slf4j // 自动生成 SLF4J 日志对象 log，方便记录日志
@RestControllerAdvice // 全局异常处理 + @ResponseBody，自动将返回值序列化为 JSON
public class GlobalExceptionHandler {

    /**
     * 处理业务异常 BusinessException
     * 当业务代码中主动抛出 BusinessException 时，由该方法处理
     */
    @ExceptionHandler(BusinessException.class) // 指定该方法处理 BusinessException 类型的异常
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage()); // 记录警告级别日志
        return Result.error(e.getCode(), e.getMessage()); // 取出异常中的错误码和消息，包装成 Result 返回
    }

    /**
     * 处理参数校验失败异常 BindException
     * 当 @Valid 注解的参数校验不通过时，Spring 会抛出 BindException
     */
    @ExceptionHandler(BindException.class) // 指定处理参数绑定异常
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 设置 HTTP 响应状态码为 400
    public Result<Void> handleBindException(BindException e) {
        // 从异常中提取第一条校验错误消息（通常是 @NotNull、@NotBlank 等注解的 message）
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return Result.badRequest(message); // 返回 400 错误响应
    }

    /**
     * 兜底异常处理：捕获所有未被上述方法处理的异常
     * 防止异常栈信息暴露给前端
     */
    @ExceptionHandler(Exception.class) // 处理所有 Exception 类型的异常（兜底）
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 设置 HTTP 响应状态码为 500
    public Result<Void> handleException(Exception e) {
        log.error("系统异常: ", e); // 记录错误级别日志，包含完整异常堆栈
        return Result.error("服务器内部错误"); // 返回模糊的错误提示，避免泄露内部细节
    }
}
