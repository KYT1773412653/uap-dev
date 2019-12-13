//package com.hexing.uap.service;
//
//import java.io.IOException;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.atomic.AtomicInteger;
//import javax.websocket.EncodeException;
//import javax.websocket.OnClose;
//import javax.websocket.OnMessage;
//import javax.websocket.OnOpen;
//import javax.websocket.Session;
//import javax.websocket.server.PathParam;
//import javax.websocket.server.ServerEndpoint;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//
//import com.hexing.uap.cfg.WebSocketConfigrator;
//import com.hexing.uap.constant.ConstantDef;
//import com.hexing.uap.model.HostLoginInfo;
//import com.hexing.uap.model.domain.WebSSHHander;
//import com.hexing.uap.rest.ssh.ConnectResponse;
//import com.jcraft.jsch.JSch;
//import com.jcraft.jsch.JSchException;
//
///**
// * Des:
// * 
// * @author hua.zhiwei<br>
// * @CreateDate 2019年11月25日
// */
//@ServerEndpoint(value = "/ssh/{id}", configurator = WebSocketConfigrator.class)
//@Component
//public class WebSSHService {
//	static Logger log = LoggerFactory.getLogger(WebSSHService.class);
//	public static AtomicInteger onlineCount = new AtomicInteger(0);
//	public static AtomicInteger websocketSessionId = new AtomicInteger(0);
//	public static Map<Long, HostLoginInfo> hostLoginInfoMap = new ConcurrentHashMap<>();
//	public static Map<Long, WebSSHHander> webSSHHanderMap = new ConcurrentHashMap<>();
////	private static CopyOnWriteArraySet<WebSSHHander> webSocketSet = new CopyOnWriteArraySet<>();
//	private static JSch jsch = new JSch();
//	
//	static {
//		HostLoginInfo hostLoginInfo = new HostLoginInfo();
//		hostLoginInfo.setPassword("uap");
//		hostLoginInfo.setHostname(ConstantDef.SSH_HOST_NAME);
//		hostLoginInfo.setPort(ConstantDef.SSH_HOST_PORT);
//		hostLoginInfo.setUsername("uap");
//		connect(hostLoginInfo);
//	}
//
//	public static ConnectResponse connect(HostLoginInfo hostLoginInfo) {
//		long connectId = websocketSessionId.incrementAndGet();
////			hander.setConnectId(connectId);
//		hostLoginInfoMap.put(connectId, hostLoginInfo);
//		ConnectResponse node = new ConnectResponse();
//		node.setStatus(ConstantDef.SSH_CONNECT_OK);
//		node.setEncoding(ConstantDef.DEFAULT_ENCODING);
//		node.setConnectId(connectId);
//		return node;
//
//	}
//
//	@OnOpen
//	public void onOpen(final Session socketSession, @PathParam("id") Long id)
//			throws JSchException, IOException, EncodeException, InterruptedException {
//		HostLoginInfo hostLoginInfo = hostLoginInfoMap.get(id);
//		com.jcraft.jsch.Session jschSession = jsch.getSession(hostLoginInfo.getUsername(), hostLoginInfo.getHostname(),
//				hostLoginInfo.getPort());
//		jschSession.setPassword(hostLoginInfo.getPassword());
//		WebSSHHander hander = new WebSSHHander();
//		hander.setJschSession(jschSession);
//		webSSHHanderMap.put(id, hander);
////		WebSSHHander hander = webSSHHanderMap.get(id);
//		hander.onOpen(socketSession);
//		onlineCount.incrementAndGet();
//	}
//
//	@OnClose
//	public void onClose(final Session socketSession, @PathParam("id") Long id) {
//		WebSSHHander hander = webSSHHanderMap.get(id);
//		hander.onClose();
//		webSSHHanderMap.remove(id);
//		hostLoginInfoMap.remove(id);
//		onlineCount.decrementAndGet();
//	}
//
//	@OnMessage
//	public void onMessage(String message, Session socketSession, @PathParam("id") Long id)
//			throws IOException, JSchException {
//		WebSSHHander hander = webSSHHanderMap.get(id);
//		hander.onMessage(message);
//
//	}
//}
