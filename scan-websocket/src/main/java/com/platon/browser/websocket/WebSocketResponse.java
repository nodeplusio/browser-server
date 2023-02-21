package com.platon.browser.websocket;

import lombok.Data;

import java.util.List;

@Data
public class WebSocketResponse {

    private List<String> hashes;

    private SubscriptionResponse<Object> response;

    private long dataTime;

    public static WebSocketResponse build(Object result, List<String> hashes, long dataTime) {
        WebSocketResponse webSocketResponse = new WebSocketResponse();
        SubscriptionResponse<Object> response = new SubscriptionResponse<>();
        Params<Object> params = new Params<>();
        params.setResult(result);
        response.setParams(params);

        webSocketResponse.setResponse(response);
        webSocketResponse.setHashes(hashes);
        webSocketResponse.setDataTime(dataTime);
        return webSocketResponse;
    }
}
