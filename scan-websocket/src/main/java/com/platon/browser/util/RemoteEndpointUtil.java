package com.platon.browser.util;

import com.alibaba.fastjson.JSON;
import com.platon.browser.websocket.ErrorCode;
import com.platon.browser.websocket.ErrorResult;
import com.platon.browser.websocket.Response;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.RemoteEndpoint;

@Slf4j
public class RemoteEndpointUtil {

    public static void sendError(RemoteEndpoint.Async asyncRemote, ErrorCode errorCode) {
        Response<Void> response = new Response<>();
        response.setError(new ErrorResult(errorCode.getCode(), errorCode.getMessage()));
        send(asyncRemote, response);
    }

    public static void send(RemoteEndpoint.Async asyncRemote, Object object) {
        try {
            asyncRemote.sendText(JSON.toJSONString(object)).get();
        } catch (Exception e) {
            log.error("发送失败", e);
            throw new RuntimeException(e);
        }
    }

}
