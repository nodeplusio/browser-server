package com.platon.browser.enums;

import lombok.Getter;

public enum Web3jProtocolEnum {
    WS("ws://"),HTTP("http://"),HTTPS("https://");
    @Getter
    private String head;
    Web3jProtocolEnum(String head){
        this.head = head;
    }
}
