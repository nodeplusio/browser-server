package com.platon.browser.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.platon.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;

/**
 *
 */
public class LongToHexSerializer extends JsonSerializer<Long>{

	@Override
	public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		if(value != null) {
			gen.writeString(Numeric.encodeQuantity(BigInteger.valueOf(value)));
		}
	}

}
