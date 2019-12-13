package com.hexing.uap.bpm.rest.task;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.flowable.common.rest.api.DataResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hexing.uap.bpm.BpmService;
import com.hexing.uap.bpm.app.model.common.ConfigDetailQuery;
import com.hexing.uap.bpm.app.model.common.PageFieldDetailResponse;
import com.hexing.uap.bpm.app.model.common.UapPageConfig;
import com.hexing.uap.bpm.app.model.common.UapPageConfigDetail;
import com.hexing.uap.bpm.app.model.common.UapPageField;
import com.hexing.uap.bpm.app.model.common.UapPageLocal;
import com.hexing.uap.bpm.app.service.pagecfg.PageConfigService;
import com.hexing.uap.bpm.app.service.pagecfg.PageFieldLocalService;
import com.hexing.uap.bpm.app.util.BpmResponseCode;
import com.hexing.uap.bpm.model.CliamTaskRequest;
import com.hexing.uap.bpm.model.CompTaskRequest;
import com.hexing.uap.bpm.model.StartProcessBean;
import com.hexing.uap.bpm.model.TaskCustomInfo;
import com.hexing.uap.bpm.rest.util.UapTokenUtil;
import com.hexing.uap.common.UapConstant;
import com.hexing.uap.common.authorization.AuthorizationInfo;
import com.hexing.uap.common.bean.CommonResponse;
import com.hexing.uap.common.bean.ModelResponse;
import com.hexing.uap.common.message.ResponseCode;
import com.hexing.uap.util.JsonUtil;
import com.hexing.uap.util.LocaleUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@RequestMapping("/app")
@Api(tags = {"Uap tasks"}, value = "提供流程启动，签收、处理、历史查询等功能")
public class UapTaskResource {


    @Autowired
    BpmService bpmService;
    @Autowired
    private PageConfigService service;
    @Autowired
    PageFieldLocalService localService;

    @ApiOperation(value = "启动流程并结束第一个任务节点", tags = {"Uap tasks"})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Indicates the task is started."),
            @ApiResponse(code = 404, message = "Indicates task start failure.")})
    @PostMapping(value = "bpm/task/start/first-node", produces = "application/json")
    public ModelResponse<StartProcessResponse> startAndFinishFirstNode(HttpServletRequest httpRequest,@RequestBody StartProcessBean bean) {
        ModelResponse<StartProcessResponse> response = new ModelResponse<>();
        String jsonData = (String) httpRequest.getAttribute("authInfo");
        AuthorizationInfo authInfo = JsonUtil.transAuthorizationInfo(jsonData);
        if (null == authInfo) {
            response.setMsgCode(ResponseCode.UNAUTHORIZED_ACCESS);
            return response;
        }
        String userNo = authInfo.getSessionUser().getNo();
        bean.setStartUserName(userNo);
        String processInstanceId = null;
        try {
            processInstanceId = bpmService.startAndFinishFirstNode(bean);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setMsgCode(ResponseCode.OPERATE_FAILURE);
            return response;
        }
        if (!StringUtils.isEmpty(processInstanceId)) {
            StartProcessResponse res = new StartProcessResponse();
            res.setProcessInstanceId(processInstanceId);
            response.setData(res);
            response.setMsgCode(ResponseCode.OPERATE_SUCCESS);
        }
        return response;
    }

    @ApiOperation(value = "启动流程", tags = {"Uap tasks"})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Indicates the task is started."),
            @ApiResponse(code = 404, message = "Indicates task start failure.")})
    @PostMapping(value = "bpm/task/start", produces = "application/json")
    public ModelResponse<StartProcessResponse> startTask(HttpServletRequest httpRequest,@RequestBody StartProcessBean bean) {
        ModelResponse<StartProcessResponse> response = new ModelResponse<>();
        String jsonData = (String) httpRequest.getAttribute("authInfo");
        AuthorizationInfo authInfo = JsonUtil.transAuthorizationInfo(jsonData);
        if (null == authInfo) {
            response.setMsgCode(ResponseCode.UNAUTHORIZED_ACCESS);
            return response;
        }
        String userNo = authInfo.getSessionUser().getNo();
        bean.setStartUserName(userNo);
        String processInstanceId = null;
        try {
            processInstanceId = bpmService.startTask(bean);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setMsgCode(ResponseCode.OPERATE_FAILURE);
            return response;
        }
        if (!StringUtils.isEmpty(processInstanceId)) {
            StartProcessResponse res = new StartProcessResponse();
            res.setProcessInstanceId(processInstanceId);
            response.setData(res);
            response.setMsgCode(ResponseCode.OPERATE_SUCCESS);
        }
        return response;
    }

    @ApiOperation(value = "完成任务", tags = {"Uap tasks"})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Indicates the task is completed."),
            @ApiResponse(code = 404, message = "Indicates task complete failure.")})
    @PostMapping(value = "bpm/task/complete", produces = "application/json")
    public CommonResponse completeTask(@RequestBody CompTaskRequest requestBean) throws Exception {
        CommonResponse response = new CommonResponse();
        try {
            bpmService.completeTask(requestBean);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setMsgCode(ResponseCode.OPERATE_FAILURE);
            return response;
        }
        response.setMsgCode(ResponseCode.OPERATE_SUCCESS);
        return response;
    }

    @ApiOperation(value = "完成委托任务", tags = {"Uap tasks"})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Indicates the task is resolved."),
            @ApiResponse(code = 404, message = "Indicates task resolve failure.")})
    @PostMapping(value = "bpm/task/resolve", produces = "application/json")
    public CommonResponse resolveTask(@RequestBody CompTaskRequest requestBean) throws Exception {
        CommonResponse response = new CommonResponse();
        try {
            String code = bpmService.resolveTask(requestBean);
            response.setMsgCode(code);
            return response;
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setMsgCode(ResponseCode.OPERATE_FAILURE);
            return response;
        }
    }

    @ApiOperation(value = "签收任务", tags = {"Uap tasks"})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Indicates the task is claimed."),
            @ApiResponse(code = 404, message = "Indicates task claim failure.")})
    @PostMapping(value = "bpm/task/claim/{taskId}", produces = "application/json")
    public CommonResponse claimTask(HttpServletRequest httpRequest,@PathVariable String taskId) {
        CommonResponse response = new CommonResponse();
        String jsonData = (String) httpRequest.getAttribute("authInfo");
        AuthorizationInfo authInfo = JsonUtil.transAuthorizationInfo(jsonData);
        if (null == authInfo) {
            response.setMsgCode(ResponseCode.UNAUTHORIZED_ACCESS);
            return response;
        }
        String userNo = authInfo.getSessionUser().getNo();
        CliamTaskRequest bean = new CliamTaskRequest();
        bean.setTaskId(taskId);
        bean.setUserName(userNo);
        try {
            bpmService.claimTask(bean);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setMsgCode(ResponseCode.OPERATE_FAILURE);
            return response;
        }
        response.setMsgCode(ResponseCode.OPERATE_SUCCESS);
        return response;
    }

    @ApiOperation(value = "取签任务", tags = {"Uap tasks"})
    @ApiResponses(value = {})
    @PostMapping(value = "bpm/task/unclaim/{taskId}", produces = "application/json")
    public CommonResponse unclaimTask(@PathVariable String taskId) {
        CommonResponse response = new CommonResponse();
        try {
            bpmService.unClaimTask(taskId);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setMsgCode(ResponseCode.OPERATE_FAILURE);
            return response;
        }
        response.setMsgCode(ResponseCode.OPERATE_SUCCESS);
        return response;
    }

    @ApiOperation(value = "委托任务", tags = {"Uap tasks"})
    @ApiResponses(value = {})
    @PostMapping(value = "bpm/task/delegate", produces = "application/json")
    public CommonResponse delegateTask(@RequestBody CliamTaskRequest bean) {
        CommonResponse response = new CommonResponse();
        try {
            bpmService.delegateTask(bean);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setMsgCode(ResponseCode.OPERATE_FAILURE);
            return response;
        }
        response.setMsgCode(ResponseCode.OPERATE_SUCCESS);
        return response;
    }

    @ApiOperation(value = "任务查询", tags = {"Uap tasks"})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Indicates the task history get success"),
            @ApiResponse(code = 404, message = "Indicates the task history get failure")})
    @PostMapping(value = "bpm/task/history", produces = "application/json")
    public ModelResponse<TaskResponse> getTask(HttpServletRequest httpRequest,
                                               @RequestBody UapTaskRequest request) throws Exception {

        ModelResponse<TaskResponse> response = new ModelResponse<>();
        TaskResponse taskResponse = new TaskResponse();
        List<String> candidateGroups = new ArrayList<>();
        String token = UapTokenUtil.getToken(httpRequest);

        String jsonData = (String) httpRequest.getAttribute("authInfo");
        AuthorizationInfo authInfo = JsonUtil.transAuthorizationInfo(jsonData);
        if (null == authInfo) {
            response.setMsgCode(ResponseCode.UNAUTHORIZED_ACCESS);
            return response;
        }
        String userNo = authInfo.getSessionUser().getNo();
        if (null == request.getByRole() || request.getByRole()) {
            // 获取用户拥有的所有角色
            candidateGroups = request.getCandidateGroups();
        }

        //查询配置信息
        Long configId = authInfo.getSessionUser().getConfigId();
        List<PageFieldDetailResponse> fieldList = new ArrayList<>();
        if (null != configId && configId >= 1) {
            try {
                List<UapPageConfig> uapPageConfigs = service.findByParentIdAndType(configId, request.getType());
                if (null == uapPageConfigs || uapPageConfigs.isEmpty() || uapPageConfigs.size() != 1) {
                    response.setMsgCode(BpmResponseCode.APP_NO_CONFIG);
                    return response;
                }
                UapPageConfig uapPageConfig = uapPageConfigs.get(0);
                ConfigDetailQuery configDetailQuery = new ConfigDetailQuery();
                configDetailQuery.setConfigId(uapPageConfig.getId());
                configDetailQuery.setState(UapConstant.UAP_COMM_ENABLED);
                List<UapPageConfigDetail> uapDetails = null;
                com.hexing.uap.common.bean.PageData<UapPageConfigDetail> uapPageFields = service.getFieldList(configDetailQuery);
                if (null != uapPageFields) {
                    uapDetails = uapPageFields.getData();
                    for (UapPageConfigDetail uapPageConfigDetail : uapDetails) {
                        PageFieldDetailResponse pageFieldDetailResponse = new PageFieldDetailResponse();
                        Long fieldId = uapPageConfigDetail.getFieldId();
                        UapPageField uapPageField = service.getFieldById(fieldId);
                        pageFieldDetailResponse.setUapPageField(uapPageField);
                        String userLocal = authInfo.getSessionUser().getLocal();
                        List<UapPageLocal> uapPageLocalList = localService.findByDetailId(uapPageConfigDetail.getId());
                        if (null != uapPageLocalList) {
                            for (UapPageLocal uapPageLocal:uapPageLocalList){
                                String local = LocaleUtil.getLocaleString(LocaleUtil.getLocaleByUserLanguage(uapPageLocal.getLocal()));
                                if (userLocal.equals(local)){
                                    uapPageConfigDetail.setDisplayName(uapPageLocal.getDisplayName());
                                }
                            }
                        }
                        pageFieldDetailResponse.setUapPageConfigDetail(uapPageConfigDetail);
                        fieldList.add(pageFieldDetailResponse);
                    }
                }

            } catch (Exception e) {
                response.setMsgCode(ResponseCode.OPERATE_FAILURE);
                return response;
            }
        }
        //组合配置信息
        List<PageFieldDetailResponse> searchInfo = new ArrayList<>();
        List<PageFieldDetailResponse> formInfo = new ArrayList<>();
        List<String> globalKeyList = new ArrayList<>();
        if (null != fieldList && fieldList.size() >= 1) {
            for (PageFieldDetailResponse pageDetail : fieldList) {
                if (pageDetail.getUapPageConfigDetail().getIsSearch() != null && pageDetail.getUapPageConfigDetail().getIsSearch().equals(UapConstant.UAP_COMM_TRUE)) {
                    searchInfo.add(pageDetail);
                }
                if (pageDetail.getUapPageConfigDetail().getIsForm() != null && pageDetail.getUapPageConfigDetail().getIsForm().equals(UapConstant.UAP_COMM_TRUE)) {
                    formInfo.add(pageDetail);
                }
                if (pageDetail.getUapPageConfigDetail().getIsGlobal() != null && pageDetail.getUapPageConfigDetail().getIsGlobal().equals(UapConstant.UAP_COMM_TRUE)) {
                    globalKeyList.add(pageDetail.getUapPageField().getField());
                }
            }
        }
        taskResponse.setFormInfo(formInfo);
        taskResponse.setSearchInfo(searchInfo);
        DataResponse<TaskCustomInfo> pageData = null;
        try {
            pageData = bpmService.getTask(userNo, candidateGroups, request.isFinished(), request.getStart(),
                    request.getLimit(), request.getCreatedAfter(), request.getCreatedBefore(), request.getDueAfter()
                    , request.getDueBefore(), request.getNameLike(), request.getProcessDefinitionNameLike(), request.getProcessInstanceId(), globalKeyList);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setMsgCode(ResponseCode.OPERATE_FAILURE);
            return response;
        }
        if (pageData != null) {
            List<TaskCustomInfo> result = pageData.getData();
            List<Map> mapList = new ArrayList<>();
            for (TaskCustomInfo taskCustomInfo : result) {
                Map<String, Object> sigleInfo = taskCustomInfo.getGlobalVar();
                if (null != sigleInfo) {
                    sigleInfo = transMap(sigleInfo, taskCustomInfo);
                } else {
                    sigleInfo = new HashMap<>();
                    sigleInfo = transMap(sigleInfo, taskCustomInfo);
                }
                mapList.add(sigleInfo);
            }
            taskResponse.setTaskCustomInfoList(mapList);
            taskResponse.setTotal(pageData.getSize());
            response.setData(taskResponse);
            response.setMsgCode(ResponseCode.OPERATE_SUCCESS);
        }

        return response;
    }

    @ApiOperation(value = "任务拽回", tags = {"Uap tasks"})
    @ApiResponses(value = {@ApiResponse(code = 200, message = "the task is revoked."),
            @ApiResponse(code = 404, message = "Indicates task rovoke failure.")})
    @PostMapping(value = "bpm/task/revoke", produces = "application/json")
    public CommonResponse revokeTask(HttpServletRequest httpRequest, @RequestBody RevokeTaskRequest param) {
        CommonResponse response = new CommonResponse();
        try {

            String jsonData = (String) httpRequest.getAttribute("authInfo");
            AuthorizationInfo authInfo = JsonUtil.transAuthorizationInfo(jsonData);
            if (null == authInfo) {
                response.setMsgCode(ResponseCode.UNAUTHORIZED_ACCESS);
                return response;
            }
            String userNo = authInfo.getSessionUser().getNo();

            bpmService.revokeTask(param.getProcessInstanceId(), userNo);
            response.setMsgCode(ResponseCode.OPERATE_SUCCESS);
            return response;
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setMsgCode(ResponseCode.OPERATE_FAILURE);
            return response;
        }
    }

    public Map transMap(Map<String, Object> map, TaskCustomInfo taskCustomInfo) {

        try {
            Field[] fiels = TaskCustomInfo.class.getDeclaredFields();
            for (Field field : fiels) {
                String name = field.getName();
                if (!name.equals("globalVar")) {
                    String key = String.join("_", name.replaceAll("([A-Z])", ",$1").split(",")).toLowerCase();
                    Class clazz = taskCustomInfo.getClass();
                    PropertyDescriptor pd = new PropertyDescriptor(name, clazz);
                    Method getMethod = pd.getReadMethod();
                    if (pd != null) {
                        Object value = getMethod.invoke(taskCustomInfo);
                        map.put(key, value);
                    }
                }
            }
            return map;
        } catch (Exception e) {
            return null;
        }
    }
}
