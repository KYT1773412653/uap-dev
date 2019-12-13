package com.hexing.uap.client.cfg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hexing.uap.client.UAPClientUtil;
import com.hexing.uap.common.authorization.AuthorizationInfo;
import com.hexing.uap.common.bean.CommonResponse;
import com.hexing.uap.common.bean.ListDataResponse;
import com.hexing.uap.common.bean.ModelResponse;
import com.hexing.uap.common.exception.UAPException;
import com.hexing.uap.common.message.APILogRequest;
import com.hexing.uap.util.HttpUtil;
import com.hexing.uap.util.JsonUtil;
import com.hexing.uap.util.MessageUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 平台接口日志处理切面
 * 
 * @author Tony
 *
 */
public abstract class AbstractMessageAspect {

	@Autowired
	MessageSource messageSource;
	@Autowired
	private ClientProperties clientProperties;

	protected abstract void pointCut();

	@Around("pointCut()")
	public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
		RequestAttributes ra = RequestContextHolder.getRequestAttributes();
		ServletRequestAttributes sra = (ServletRequestAttributes) ra;
		HttpServletRequest request = sra.getRequest();
//		String apiId = String.valueOf(request.getAttribute(HttpUtil.CURR_API_ID));
		String jsonData = (String) request.getAttribute("authInfo");
		AuthorizationInfo authInfo = JsonUtil.transAuthorizationInfo(jsonData);
		String apiId = authInfo.getAuthRestApi().getId();
		Long userId = null;
		if(authInfo.getSessionUser()!=null) {
			userId = authInfo.getSessionUser().getId();
		}
		
		String token = HttpUtil.getToken(request);
		Object[] args = pjp.getArgs();
		APILogRequest logRequest = beforeProceed(request, args, userId, apiId);
		String logState = String.valueOf(request.getAttribute(HttpUtil.LOG_STATE));
		logRequest.setLogState(logState);
		Object result = null;
		try {
			result = pjp.proceed();
			return handleSuccess(logRequest, result, messageSource, request, token);
		} catch (Exception e) {
			handleException(logRequest, e, messageSource, request, token);
		}
		return result;
	}

	protected void saveAPILogRequest(APILogRequest log, String token) throws Throwable {
		UAPClientUtil.saveLog(clientProperties.getUapUrl(), token,log);
	};

	protected APILogRequest beforeProceed(HttpServletRequest request, Object[] args, Long userId, String apiId)
			throws Throwable {
		String url = request.getMethod() + ":" + request.getServletPath();
		Long accessTime = System.currentTimeMillis();
		String param = null;
		APILogRequest logRequest = new APILogRequest(userId, accessTime, apiId, url, param);
		try {
			param = getRequestParam(args);
			logRequest.setParam(param);
		} catch (UAPException e) {
			logRequest.setResult(e.getExceptionCode());
		}
		return logRequest;
	}

	protected Object handleSuccess(APILogRequest logRequest, Object result, MessageSource messageSource,
			HttpServletRequest request, String token) throws Throwable {
		if (logRequest != null) {
			logRequest.setEndTime(System.currentTimeMillis());
		}
		Object newResponse = getLocalResponse(result, request, messageSource);
		String jsonData = JsonUtil.getMapper().writeValueAsString(newResponse);
		if (logRequest != null) {
			int length = jsonData.length();
			if(length>256){
				length = 256;
			}
			String logres = jsonData.substring(0,length);
			logRequest.setResult(logres+"…………(only save 256byte)");
			logRequest.setRemark("success");
			saveAPILogRequest(logRequest, token);
		}
		return newResponse;
	}

	protected void handleException(APILogRequest logRequest, Exception e, MessageSource messageSource,
			HttpServletRequest request, String token) throws Throwable {
		if (logRequest != null) {
			logRequest.setEndTime(System.currentTimeMillis());
		}
		String errorMsg = e.getMessage();
		if (e instanceof UAPException) {
			String msgCode = ((UAPException) e).getExceptionCode();
			String bizMessage = MessageUtil.getMessage(messageSource, request, msgCode,
					((UAPException) e).getBizMessage());
			((UAPException) e).setBizMessage(bizMessage);
			errorMsg = msgCode + ":" + bizMessage;
		}
		if (logRequest != null) {
			logRequest.setResult(errorMsg);
			logRequest.setRemark("excception");
			saveAPILogRequest(logRequest, token);
		}
		throw e;
	}

	public Object getLocalResponse(Object result, HttpServletRequest request, MessageSource messageSource) {
		// Object newResponse = null;
		if (result != null && result instanceof ModelResponse) {
			ModelResponse<?> response = (ModelResponse<?>) result;
			String msgCode = response.getMsgCode();
			String msg = response.getMessage();
			String localMsg = MessageUtil.getMessage(messageSource, request, msgCode, msg);
			response.setMessage(localMsg);
			return response;
		} else if (result != null && result instanceof ListDataResponse) {
			ListDataResponse<?> response = (ListDataResponse<?>) result;
			String msgCode = response.getMsgCode();
			String msg = response.getMessage();
			String localMsg = MessageUtil.getMessage(messageSource, request, msgCode, msg);
			response.setMessage(localMsg);
			return response;
		} else if (result != null && result instanceof CommonResponse) {
			CommonResponse response = (CommonResponse) result;
			String msgCode = response.getMsgCode();
			String msg = response.getMessage();
			String localMsg = MessageUtil.getMessage(messageSource, request, msgCode, msg);
			response.setMessage(localMsg);
			return response;
		} else {
			return result;
		}
	}

	public String getRequestParam(Object[] args) {
		if (args == null || args.length == 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (Object object : args) {
			String parameType = "";
			String paraValue = "";
			if (object == null) {
				parameType = "null";
				paraValue = "null";
			} else if (object instanceof HttpServletRequest) {
				parameType = HttpServletRequest.class.getSimpleName();
			} else if (object instanceof HttpServletResponse) {
				parameType = HttpServletResponse.class.getSimpleName();
			} else {
				parameType = object.getClass().getSimpleName();
				try {
					paraValue = JsonUtil.getMapper().writeValueAsString(object);
				} catch (JsonProcessingException e) {
					paraValue = object.toString();
				}
			}
			sb.append("[");
			sb.append(parameType);
			sb.append("]=");
			sb.append(paraValue);
			sb.append(";");
		}
		return sb.toString();
	}
}