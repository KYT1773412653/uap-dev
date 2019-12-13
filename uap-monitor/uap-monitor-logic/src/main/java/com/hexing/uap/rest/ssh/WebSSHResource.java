//package com.hexing.uap.rest.ssh;
//
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.hexing.uap.constant.ConstantDef;
//import com.hexing.uap.constant.ResponseCode;
//import com.hexing.uap.model.HostLoginInfo;
//import com.hexing.uap.rest.ModelResponse;
//import com.hexing.uap.service.WebSSHService;
//
///**
// * Des:
// * 
// * @author hua.zhiwei<br>
// * @CreateDate 2019年11月25日
// */
//@RestController
//public class WebSSHResource {
//
//	@PostMapping(value = "/ssh/connect", produces = "application/json")
//	public ModelResponse<ConnectResponse> connect(@RequestParam("file") MultipartFile file,
//			ConnectRequest connectRequest) {
//		ModelResponse<ConnectResponse> response = new ModelResponse<ConnectResponse>();
//		response.setMsgCode(ResponseCode.OPERATE_SUCCESS);
//		HostLoginInfo hostLoginInfo = new HostLoginInfo();
//		hostLoginInfo.setPassword(connectRequest.getPassword());
//		hostLoginInfo.setHostname(ConstantDef.SSH_HOST_NAME);
//		hostLoginInfo.setPort(null == connectRequest.getPort() ? ConstantDef.SSH_HOST_PORT : connectRequest.getPort());
//		hostLoginInfo.setUsername(connectRequest.getUsername());
//		hostLoginInfo.setPrivatekey(file);
//		ConnectResponse node = WebSSHService.connect(hostLoginInfo);
//		if (null == node) {
//			response.setMsgCode(ResponseCode.OPERATE_FAILURE);
//		}
////        node.put("status", 0);
////        node.put("id", wsId);
////        node.put("encoding", "utf-8");
//		response.setData(node);
//		return response;
//	}
//}
