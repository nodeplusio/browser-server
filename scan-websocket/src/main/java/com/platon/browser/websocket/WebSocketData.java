package com.platon.browser.websocket;

import lombok.Data;

@Data
public class WebSocketData {

    private String requestHash;

    private String responseChannel;

    private Request request;

    private SubscriptionResponse<Object> response;

    private String lastPushData;

    private long dataTime;
}
