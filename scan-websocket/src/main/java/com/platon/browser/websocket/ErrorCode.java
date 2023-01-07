package com.platon.browser.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    PARSE_ERROR(-32700, "Parse error");

    int code;
    String message;
}
