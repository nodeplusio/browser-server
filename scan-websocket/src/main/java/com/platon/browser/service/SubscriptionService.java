package com.platon.browser.service;

import com.platon.browser.util.RemoteEndpointUtil;
import com.platon.browser.websocket.Params;
import com.platon.browser.websocket.Request;
import com.platon.browser.websocket.SubscriptionResponse;
import com.platon.browser.websocket.WebSocketData;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.util.Map;

public interface SubscriptionService {
    void subscribe(Map.Entry<Session, WebSocketData> entry, Map.Entry<String, Request> request);

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

}
