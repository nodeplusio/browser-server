package com.platon.browser.websocket;

import com.alibaba.fastjson.JSON;
import com.platon.browser.service.WebSocketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@ServerEndpoint("/websocket/{type}")
public class WebSocketServerEndpoint {

    private static final String ETH_SUBSCRIBE = "eth_subscribe";
    private static final String ETH_UNSUBSCRIBE = "eth_unsubscribe";
    private static final List<String> SUPPORT_METHODS = Arrays.asList(ETH_SUBSCRIBE, ETH_UNSUBSCRIBE);

    private static ApplicationContext applicationContext;

    private static WebSocketService webSocketService;

    @Autowired
    public static void setApplicationContext(ApplicationContext applicationContext) {
        WebSocketServerEndpoint.applicationContext = applicationContext;
    }

    @Autowired
    public static void setWebSocketService(WebSocketService webSocketService) {
        WebSocketServerEndpoint.webSocketService = webSocketService;
    }

    /**
     * 连接建立
     * @param session
     * @param config
     * @param type
     */
    @OnOpen
    public void onOpen(Session session, EndpointConfig config, @PathParam(value = "type") String type) {
        log.debug(type);
        WebSocketService.put(session, type);
    }

    /**
     * 连接关闭
     * @param session
     * @param reason
     */
    @OnClose
    public void onClose(Session session, CloseReason reason) {
        WebSocketService.remove(session);
    }

    /**
     * 接收文本信息
     * @param session
     * @param message
     */
    @OnMessage
    public void onMessage(Session session, String message) {
        log.debug(message);
        RemoteEndpoint.Async asyncRemote = session.getAsyncRemote();
        Request request;
        try {
            request = JSON.parseObject(message, Request.class);
        } catch (Exception e) {
            Response<Void> response = new Response<>();
            response.setError(new ErrorResult(-32700, "Parse error"));
            asyncRemote.sendText(JSON.toJSONString(response));
            return;
        }
        if (request == null || !SUPPORT_METHODS.contains(request.getMethod())) {
            Response<Void> response = new Response<>();
            response.setError(new ErrorResult(-32700, "Parse error"));
            asyncRemote.sendText(JSON.toJSONString(response));
            return;
        }
        WebSocketData webSocketData = WebSocketService.get(session);
        Map<String, Request> requests = webSocketData.getRequests();
        if (ETH_SUBSCRIBE.equals(request.getMethod())) {
            Response<String> response = new Response<>();
            response.setId(request.getId());
            response.setJsonrpc(request.getJsonrpc());
            String result = "0x" + UUID.randomUUID().toString().replaceAll("-", "");
            requests.put(result, request);
            response.setResult(result);
            asyncRemote.sendText(JSON.toJSONString(response));
        } else if (ETH_UNSUBSCRIBE.equals(request.getMethod())) {
            Response<Boolean> response = new Response<>();
            response.setId(request.getId());
            response.setJsonrpc(request.getJsonrpc());
            response.setResult(true);
            requests.remove((String) request.getParams().get(0));
            asyncRemote.sendText(JSON.toJSONString(response));
        }
    }

    /**
     * 异常处理
     * @param session
     * @param e
     */
    @OnError
    public void onError(Session session, Throwable e) {
        log.error("web socket error.", e);
        WebSocketService.remove(session);
    }
}

