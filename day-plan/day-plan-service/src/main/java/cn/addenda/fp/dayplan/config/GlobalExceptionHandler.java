package cn.addenda.fp.dayplan.config;

import cn.addenda.component.jdk.exception.ServiceException;
import cn.addenda.component.jdk.exception.SystemException;
import cn.addenda.component.jdk.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  private static final String MSG_5XX_1 = "url：[{}]，异常类型：[5XX，组件异常]，信息：[{}]。";
  private static final String MSG_5XX_2 = "url：[{}]，异常类型：[5XX，未知异常]，信息：[{}]。";
  private static final String MSG_4XX = "url：[{}]，异常类型：[4XX]，信息：[{}]。";
  private static final String MSG_2XX = "url：[{}]，异常类型：[2XX]，信息：[{}]。";

  @ExceptionHandler(SystemException.class)
  public Object handleException(SystemException ex, HttpServletRequest request, HttpServletResponse response) {
    log.error(MSG_5XX_1, request.getRequestURI(), ex.getMessage(), ex);
    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

    Result<String> result = new Result<>();
    result.setReqCode(Result.FAILED);
    result.setReqMessage("系统异常，请联系IT处理！");
    result.setResult(null);
    return result;
  }

  @ExceptionHandler({ServiceException.class, IllegalArgumentException.class})
  public Object handleException(ServiceException ex, HttpServletRequest request) {
    log.info(MSG_2XX, request.getRequestURI(), ex.getMessage(), ex);

    Result<String> result = new Result<>();
    result.setReqCode(Result.FAILED);
    result.setReqMessage(ex.getMessage());
    result.setResult(null);
    return result;
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public Object handleException(NoHandlerFoundException ex, HttpServletRequest request, HttpServletResponse response) {
    log.warn(MSG_4XX, request.getRequestURI(), ex.getMessage(), ex);
    response.setStatus(HttpServletResponse.SC_NOT_FOUND);

    Result<String> result = new Result<>();
    result.setReqCode(Result.FAILED);
    result.setReqMessage(String.format("[%s] not found!", request.getRequestURI()));
    result.setResult(null);
    return result;
  }

  @ExceptionHandler(ServletRequestBindingException.class)
  public Object handleException(ServletRequestBindingException ex, HttpServletRequest request, HttpServletResponse response) {
    log.warn(MSG_4XX, request.getRequestURI(), ex.getMessage(), ex);
    response.setStatus(HttpServletResponse.SC_NOT_FOUND);

    Result<String> result = new Result<>();
    result.setReqCode(Result.FAILED);
    result.setReqMessage(String.format("[%s] not found!", request.getRequestURI()));
    result.setResult(null);
    return result;
  }

  @ExceptionHandler(Exception.class)
  public Object handleException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
    log.error(MSG_5XX_2, request.getRequestURI(), ex.getMessage(), ex);
    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

    Result<String> result = new Result<>();
    result.setReqCode(Result.FAILED);
    result.setReqMessage("系统异常，请联系IT处理！");
    result.setResult(null);
    return result;
  }

}

