package com.hexing.uap.schedule.execute.ms.service;

import java.net.URI;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import com.hexing.uap.client.MicroInstanceService;
import com.hexing.uap.client.cfg.ClientProperties;
import com.hexing.uap.common.bean.CommonResponse;
import com.hexing.uap.common.bean.ModelResponse;
import com.hexing.uap.common.session.SessionUser;
import com.hexing.uap.schedule.logic.message.ScheduleDef;
import com.hexing.uap.schedule.logic.message.TaskMessage;
import com.hexing.uap.schedule.logic.model.domain.RestApiResponse;
import com.hexing.uap.schedule.logic.model.jpa.UapJobJpa;
import com.hexing.uap.schedule.logic.service.AbsRestApiService;
import com.hexing.uap.schedule.logic.service.cache.TokenCache;
import com.hexing.uap.schedule.logic.util.LogUtil;
import com.hexing.uap.schedule.logic.util.ThreadLocalUtil;
import com.hexing.uap.util.JsonUtil;

/**
 * Des:
 * 
 * @author hua.zhiwei<br>
 * @CreateDate 2019年6月14日
 */
@Service
public class RestApiService extends AbsRestApiService {
	
	@Autowired
	public ClientProperties clientProperties;
	
	@Autowired
	MicroInstanceService microInstanceService;
	
	@Autowired
	TokenCache tokenCache;

	@Override
	protected String callRestApiOfSliceJob(RestApiResponse uapRestApi, UapJobJpa jobJpa,
			Long logId, String token) {
		final StringBuilder execLog = (StringBuilder) ThreadLocalUtil.get(ScheduleDef.EXEC_LOG);
		LogUtil.write(execLog, "This is a slice task");
		log.info("This is a slice task");
		List<URI> executorList = getExecutor(uapRestApi.getUapApp().getCode());
		if (CollectionUtils.isEmpty(executorList)) {
			LogUtil.write(execLog, "Error:There is not exist available executor");
			log.error("There is not exist available executor");
			return TaskMessage.NO_EXECUTOR;
		}
		//updateExecHost(logId, executorList.get(0));
		updateSliceInfo(executorList, logId);
		String url = uapRestApi.getUrl().split(":")[1];
		int totalSlice = executorList.size();
		for (int slice = 1; slice <= totalSlice; slice++) {
			String jsonParam = parseParam(jobJpa.getRestApiId(), jobJpa.getRestParam(), logId, totalSlice, slice);
			URI appUri = executorList.get(slice - 1);
			asyncHttpPostUnBalanced(appUri.resolve(url), token, jsonParam, logId, slice);
		}
		return TaskMessage.OPERATE_SUCCESS;
	}

	@Override
	public ModelResponse<String> getToken() {
		final StringBuilder execLog = (StringBuilder) ThreadLocalUtil.get(ScheduleDef.EXEC_LOG);
		ModelResponse<String> response = new ModelResponse<String>();
		response.setMsgCode(TaskMessage.OPERATE_SUCCESS);
		String token = tokenCache.getTokenCache();
		if (null != token) {
			response.setData(token);
			return response;
		}
		while (!redisUtil.lock(JOB_TOKEN_LOCK, LOCK_EXPIRE_TIME)) {
			try {
				Thread.sleep(GET_LOCK_WAIT_TIME);
			} catch (InterruptedException e) {
				response.setMsgCode(TaskMessage.CAN_NOT_GET_TOKEN);
				response.setMessage("can not get app token");
				return response;
			}
		}
		token = tokenCache.getTokenCache();
		if (null != token) {
			response.setData(token);
			redisUtil.release(JOB_TOKEN_LOCK);
			return response;
		}
		LogUtil.write(execLog, "There is no local cache of token, now it's time to get token");
		response.setMsgCode(TaskMessage.CAN_NOT_GET_TOKEN);
		response.setMessage("can not get app token");
		try {
			URI uri = microInstanceService.getUri(clientProperties.getInstanceHostName());
			//String url = "http://" + instance.getServiceId();
			String appCode = clientProperties.getAppCode();
			String appScret = clientProperties.getAppScret();
			log.info("appCode [{}]", appCode);
			LogUtil.write(execLog, "AppLogin begin,, appCode is [{1}]", appCode);
			ModelResponse<SessionUser> user = microInstanceService.appLogin(uri, appCode, appScret);
			if (null == user) {
				log.error("Error encountered in getting token, response is null");
				LogUtil.write(execLog, "Error encountered in getting token, response is null");
				return response;
			}
			if (!TaskMessage.OPERATE_SUCCESS.equals(user.getMsgCode())) {
				log.error("Error encountered in getting token, msgCode [{}]", user.getMsgCode());
				response.setMsgCode(user.getMsgCode());
				response.setMessage(user.getMessage());
				LogUtil.write(execLog, "Error encountered in getting token, msgCode [{}]", user.getMsgCode());
				return response;
			}
			if (null == user.getData()) {
				log.error("Error encountered in getting token, SessionUser is null");
				LogUtil.write(execLog, "Error encountered in getting token, SessionUser is null");
				return response;
			}
			token = user.getData().getToken();
			tokenCache.setTokenCache(token);
			response.setData(token);
			response.setMsgCode(TaskMessage.OPERATE_SUCCESS);
			response.setMessage("operate success");
			LogUtil.write(execLog, "Get token success");
			return response;
		} catch (Exception e1) {
			log.error("Error encountered in getting token , error message [{0}]", e1.getMessage());
			LogUtil.write(execLog, "Error encountered in getting token, error message [{}]",
					StringUtils.substring(e1.getMessage(), 0, LogUtil.ERROR_MESSAGE_LENGTH));
			return response;
		} finally {
			redisUtil.release(JOB_TOKEN_LOCK);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected RestApiResponse getRestApi(String token, String restApiId) {
		String resStr = null;
		String url = FIND_REST_API_BY_ID + restApiId;
		final StringBuilder execLog = (StringBuilder) ThreadLocalUtil.get(ScheduleDef.EXEC_LOG);
		RestApiResponse restApiResponse = new RestApiResponse();
		restApiResponse.setMsgCode(TaskMessage.QUERY_REST_API_ERROR);
		restApiResponse.setMessage("Failed to find restApi information");
		try {
			String hostName = clientProperties.getInstanceHostName();
			URI uri = microInstanceService.getUri(hostName);
			log.info("token [{}]", token);
			log.info("find api by id, hostName [{}], apiUrl [{}]", hostName, url);
			LogUtil.write(execLog, "Find restApi by id begin, appUrl is  [{0}], restApi url is [{1}]", hostName, url);
			resStr = microInstanceService.getUnBalanced(uri.resolve(url) , token);
			log.info("findApiById result is [{}]", resStr);
			ModelResponse<RestApiResponse> response = JsonUtil.getModelResponse(resStr, RestApiResponse.class);
			if (null != response) {
				restApiResponse = response.getData();
				restApiResponse.setMsgCode(response.getMsgCode());
				//restApiResponse.setMessage(response.getMessage());
			}
			return restApiResponse;
		} catch (Exception e) {
			log.error("Error encountered in finding restApi by id, exception message is [{0}]", e.getMessage());
			LogUtil.write(execLog, "Error encountered in finding restApi by id, exception message is [{0}]",
					StringUtils.substring(e.getMessage(), 0, LogUtil.ERROR_MESSAGE_LENGTH));
			return restApiResponse;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected ModelResponse<String> getCalendar(Long tenantId, String token, String jsonParam) {
		final StringBuilder execLog = (StringBuilder) ThreadLocalUtil.get(ScheduleDef.EXEC_LOG);
		String hostName = clientProperties.getInstanceHostName();
		URI uri = microInstanceService.getUri(hostName);
		String resStr = null;
		try {
			log.info("appUrl [{}], apiUrl [{}]", hostName, JUDGE_CALENDAR_URL);
			LogUtil.write(execLog, "Call restApi of judge calendar begin, hostName is [{0}], apiUrl is [{1}]", hostName,
					JUDGE_CALENDAR_URL);
			resStr = microInstanceService.postUnBalanced(uri.resolve(JUDGE_CALENDAR_URL) , token, jsonParam);
			return JsonUtil.getModelResponse(resStr, String.class);
		} catch (Exception e) {
			LogUtil.write(execLog, "Error encountered in posting rest api, apiUrl [{0}], error message [{1}]",
					JUDGE_CALENDAR_URL, StringUtils.substring(e.getMessage(), 0, LogUtil.ERROR_MESSAGE_LENGTH));
			log.error("Error encountered in posting rest api, apiUrl [{0}], error message [{1}]", JUDGE_CALENDAR_URL,
					e.getMessage());
			return null;
		}
	}
	
	@Override
	protected String callRestApiOfJob(RestApiResponse uapRestApi, UapJobJpa jobJpa, Long logId, String token) {
		String url = uapRestApi.getUrl().split(":")[1];
		log.debug("url [{}]", url);
		URI uri = microInstanceService.getUri(uapRestApi.getUapApp().getCode());
		//String appUrl = "http://" + instance.getServiceId();
//		if (appUrl.endsWith("/")) {
//			appUrl = appUrl.substring(0, appUrl.length() - 1);
//		}
//		String apiUrl = appUrl + url;
		updateExecHost(logId, uri);
		String jsonParam = parseParam(jobJpa.getRestApiId(), jobJpa.getRestParam(), logId);
		asyncHttpPost(uri.resolve(url), token, jsonParam, logId);
		return TaskMessage.OPERATE_SUCCESS;
	}
	
	protected void asyncHttpPost(URI apiUrl, String token, String jsonParam, Long logId) {
		System.out.println("apiUrl " + apiUrl);
		// 使用异步HTTP请求调用远程job接口。
		microInstanceService.asyncPostUnBalanced(apiUrl, token, jsonParam).subscribe(result -> {
			try {
				log.info("result from job restapi [{}]", result);
				if (!TaskMessage.JOB_RESTAPI_TIMEOUT.equals(result)) {
					CommonResponse response = JsonUtil.getCommonResponse(result);
					if (!TaskMessage.OPERATE_SUCCESS.equals(response.getMsgCode())) {
						log.error("Error encountered in calling job restApi, msgCode [{}]", response.getMsgCode());
						jobLogService.updateExecError(logId, response.getMsgCode(), result, null);
					} else {
						jobLogService.updateExecSuccess(logId, null);
					}
					log.info("response msgcode ", response.getMsgCode());
				}
			} catch (Exception e) {
				log.error("Error encountered in calling job restApi, message [{}]", e.getMessage());
				jobLogService.updateExecError(logId, TaskMessage.POST_REST_API_URL_ERROR,
						StringUtils.substring(e.getMessage(), 0, LogUtil.ERROR_MESSAGE_LENGTH), null);
			}
		});
	}
	
	protected List<URI> getExecutor(String code) {
		List<URI> result = Lists.newArrayList();
		List<ServiceInstance> instanceList = microInstanceService.getAllServiceInstance(code);
		if (CollectionUtils.isEmpty(instanceList)) {
			return result;
		}
		for (ServiceInstance instance : instanceList) {
			result.add(instance.getUri());
		}
		return result;
	}
	
	private void asyncHttpPostUnBalanced(URI uri, String token, String jsonParam, Long logId, int slice) {
		// 使用异步HTTP请求调用远程job接口。
		microInstanceService.asyncPostUnBalanced(uri, token, jsonParam).subscribe(result -> {
			try {
				log.info("result from job restapi [{}]", result);
				if (!TaskMessage.JOB_RESTAPI_TIMEOUT.equals(result)) {
					CommonResponse response = JsonUtil.getCommonResponse(result);
					if (!TaskMessage.OPERATE_SUCCESS.equals(response.getMsgCode())) {
						log.error("Error encountered in calling job restApi, msgCode [{}]", response.getMsgCode());
						jobLogService.updateExecError(logId, response.getMsgCode(), result, slice);
					} else {
						jobLogService.updateExecSuccess(logId, slice);
					}
					log.info("response msgcode ", response.getMsgCode());
				}
			} catch (Exception e) {
				log.error("Error encountered in calling job restApi, message [{}]", e.getMessage());
				jobLogService.updateExecError(logId, TaskMessage.POST_REST_API_URL_ERROR,
						StringUtils.substring(e.getMessage(), 0, LogUtil.ERROR_MESSAGE_LENGTH), slice);
			}
		});
	}

}
