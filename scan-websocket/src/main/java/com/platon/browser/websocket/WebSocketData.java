package com.platon.browser.websocket;

import lombok.Data;

import java.util.List;

@Data
public class WebSocketData {

    private String requestHash;
    private List<String> hashes;

    private String responseChannel;

    private Request request;

    private SubscriptionResponse<Object> response;

    private String lastPushData;

    private long dataTime;
}
