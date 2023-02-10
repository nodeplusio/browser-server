package com.platon.browser.service;

import com.platon.browser.util.RemoteEndpointUtil;
import com.platon.browser.websocket.*;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.util.Map;

public interface SubscriptionService {

    default void send(Map.Entry<Session, WebSocketData> entry, Map.Entry<String, Request> request, Object result) {
        SubscriptionResponse<Object> response = new SubscriptionResponse<>();
        response.setJsonrpc(request.getValue().getJsonrpc());
        response.setMethod("eth_subscription");
        Params<Object> params = new Params<>();
        params.setSubscription(request.getKey());
        params.setResult(result);
        response.setParams(params);
        RemoteEndpoint.Async asyncRemote = entry.getKey().getAsyncRemote();
        RemoteEndpointUtil.send(asyncRemote, response);
    }

    void subscribe(WebSocketChannelData webSocketData);
}
