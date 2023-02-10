package com.platon.browser.websocket;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class WebSocketChannelData {

    private String requestHash;

    private String responseChannel;

    private Request request;

    private SubscriptionResponse<Object> response;

    private Long blockNum;

    private List<String> hashes = new ArrayList<>();
}
