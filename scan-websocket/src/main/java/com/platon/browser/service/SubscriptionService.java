package com.platon.browser.service;

import com.platon.browser.websocket.Request;
import com.platon.browser.websocket.WebSocketData;

import javax.websocket.Session;
import java.util.Map;

public interface SubscriptionService {
    void push(Map.Entry<Session, WebSocketData> entry, Map.Entry<String, Request> request);
}
