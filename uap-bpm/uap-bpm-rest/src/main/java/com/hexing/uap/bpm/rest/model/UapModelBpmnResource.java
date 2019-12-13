/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hexing.uap.bpm.rest.model;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hexing.uap.common.bean.CommonResponse;
import com.hexing.uap.common.message.ResponseCode;

import io.swagger.annotations.Api;

/**
 * @author jbarrez
 */
@RestController
@RequestMapping("/app")
@Api(tags = { "ProcessModels" }, value = "Process models and apps")
public class UapModelBpmnResource extends UapAbstractModelBpmnResource {

    /**
     * GET /rest/models/{modelId}/bpmn20 -> Get BPMN 2.0 xml
     */
    @RequestMapping(value = "/rest/models/{processModelId}/bpmn20", method = RequestMethod.GET)
    public CommonResponse getProcessModelBpmn20Xml(HttpServletResponse httpServletResponse, @PathVariable String processModelId) {
        CommonResponse response = new CommonResponse();
        try {
            super.getProcessModelBpmn20Xml(httpServletResponse, processModelId);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setMsgCode(ResponseCode.OPERATE_FAILURE);
            return response;
        }
        response.setMsgCode(ResponseCode.OPERATE_SUCCESS);
        return response;
    }

    /**
     * GET /rest/models/{modelId}/history/{processModelHistoryId}/bpmn20 -> Get BPMN 2.0 xml
     */
    @RequestMapping(value = "/rest/models/{processModelId}/history/{processModelHistoryId}/bpmn20", method = RequestMethod.GET)
    public CommonResponse getHistoricProcessModelBpmn20Xml(
            HttpServletResponse httpServletResponse, @PathVariable String processModelId, @PathVariable String processModelHistoryId){
        CommonResponse response = new CommonResponse();
        try {
            super.getHistoricProcessModelBpmn20Xml(httpServletResponse, processModelId, processModelHistoryId);
        } catch (Exception e) {
            response.setMessage(e.getMessage());
            response.setMsgCode(ResponseCode.OPERATE_FAILURE);
            return response;
        }
        response.setMsgCode(ResponseCode.OPERATE_SUCCESS);
        return response;
    }

}
