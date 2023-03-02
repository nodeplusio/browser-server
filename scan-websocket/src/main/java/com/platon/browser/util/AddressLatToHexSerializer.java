package com.platon.browser.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.platon.bech32.Bech32;
import com.platon.parameters.NetworkParameters;

import java.io.IOException;

/**
 *
 */
public class AddressLatToHexSerializer extends JsonSerializer<String>{

	@Override
	public void serialize(String value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		if(value != null && value.startsWith("lat")) {
			NetworkParameters.selectPlatON();
			String hex =  Bech32.addressDecodeHex(value);
			gen.writeString(hex);
		}
	}

}
