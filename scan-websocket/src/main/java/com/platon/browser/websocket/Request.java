package com.platon.browser.websocket;

import lombok.Data;

import java.util.List;

@Data
public class Request{
	private String method;
	private Integer id;
	private String jsonrpc;
	private List<Object> params;
}