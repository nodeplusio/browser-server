package com.platon.browser.service;

import com.platon.browser.websocket.WebSocketData;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebSocketService {
    private static final Map<Session, WebSocketData> MAP = new ConcurrentHashMap<>();

    public static void put(Session session, String type) {
        WebSocketData value = new WebSocketData();
        value.setType(type);
        MAP.put(session, value);
    }

    public static void remove(Session session) {
        MAP.remove(session);
    }

    public static WebSocketData get(Session session) {
        return MAP.get(session);
    }
    public static Map<Session, WebSocketData> getMap() {
        return MAP;
    }
}
