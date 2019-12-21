package com.liteam.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.Set;


/**
 * 全局处理所有的运行时异常
 *
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    private static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private JsonResult jsonResult = new JsonResult();

    /**
     * 400BadRequest
     *
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Object handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        logger.error("缺少请求参数:{}", e.getMessage());
        return jsonResult.failed(400, "缺少请求参数", HttpStatus.BAD_REQUEST);
    }

    /**
     * 400 - Bad Request
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Object handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        logger.error("参数解析失败:{}", e.getMessage());
        return jsonResult.failed(400, "参数解析失败", HttpStatus.BAD_REQUEST);
    }

    /**
     * 400 - Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        logger.error("参数验证失败:{}", e.getMessage());
        BindingResult result = e.getBindingResult();
        FieldError error = result.getFieldError();
        String code = error.getDefaultMessage();
        String message = String.format("%s", code);
        return jsonResult.failed(400, "参数验证失败" + message, HttpStatus.BAD_REQUEST);
    }

    /**
     * 400 - Bad Request
     */
    @ExceptionHandler(BindException.class)
    public Object handleBindException(BindException e) {
        logger.error("参数绑定失败:{}", e.getMessage());
        BindingResult result = e.getBindingResult();
        FieldError error = result.getFieldError();
        String code = error.getDefaultMessage();
        String message = String.format("%s", code);
        return jsonResult.failed(400, "参数绑定失败" + message, HttpStatus.BAD_REQUEST);
    }


    /**
     * 400 - Bad Request
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public Object handleServiceException(ConstraintViolationException e) {
        logger.error("参数验证失败:{}", e.getMessage());
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        ConstraintViolation<?> violation = violations.iterator().next();
        String message = violation.getMessage();
        return jsonResult.failed(400, "参数验证失败" + message, HttpStatus.BAD_REQUEST);
    }

    /**
     * 400 - Bad Request
     */
    @ExceptionHandler(ValidationException.class)
    public Object handleValidationException(ValidationException e) {
        logger.error("参数验证失败:{}", e.getMessage());
        return jsonResult.failed(400, "参数验证失败", HttpStatus.BAD_REQUEST);
    }

    /**
     * 405 - Method Not Allowed
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Object handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        logger.error("不支持当前请求方法:{}", e.getMessage());
        return jsonResult.failed(405, "不支持当前请求方法", HttpStatus.METHOD_NOT_ALLOWED);
    }

    /**
     * 415 - Unsupported Media Type
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public Object handleHttpMediaTypeNotSupportedException(Exception e) {
        logger.error("不支持当前媒体类型:{}", e.getMessage());
        return jsonResult.failed(415, "不支持当前媒体类型", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    /**
     * 500 - Internal Server Error
     */
    //@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e) {
        logger.error("服务器异常 :{}", e);
        return jsonResult.failed(500, "服务器异常", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
