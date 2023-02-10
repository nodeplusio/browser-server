package com.platon.browser.websocket;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class WebSocketData {

    private String type;

    private Map<String, Request> requests = new HashMap<>();

    private WebSocketChannelData webSocketChannelData;

    private Long blockNum;

    private List<String> hashes = new ArrayList<>();

}
