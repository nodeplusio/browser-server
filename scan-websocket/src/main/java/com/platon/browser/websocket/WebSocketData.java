package com.platon.browser.websocket;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class WebSocketData {

    private String type;

    private Map<String, Request> requests = new HashMap<>();

    private Long blockNum;
}
