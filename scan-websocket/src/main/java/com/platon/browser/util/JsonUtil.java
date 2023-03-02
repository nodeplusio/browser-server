package com.platon.browser.util;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TimeZone;

/**
 * Json工具类
 */
public class JsonUtil {

    private static final JsonMapper useAnnotationMapper = buildMapper();
    private static final JsonMapper excludeNullMapper = buildMapper();

    static {
        excludeNullMapper.setSerializationInclusion(Include.NON_NULL);
    }

    private JsonUtil() {
    }

    public static ObjectMapper getUseAnnotationMapper() {
        return useAnnotationMapper;
    }

    private static JsonMapper buildMapper() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZZZ");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return JsonMapper.builder().configure(MapperFeature.USE_ANNOTATIONS, true)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
                .configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
                .configure(Feature.ALLOW_SINGLE_QUOTES, true)
                .configure(Feature.IGNORE_UNDEFINED, true)
                .configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .defaultDateFormat(dateFormat)
                .addModule(buildDateModule())
                .addModule(new JavaTimeModule())
                .build();

    }

    private static SimpleModule buildDateModule() {
        SimpleModule module = new SimpleModule("dateTime", PackageVersion.VERSION);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(dateTimeFormatter));
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(dateTimeFormatter));
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        module.addSerializer(LocalDate.class, new LocalDateSerializer(dateFormatter));
        module.addDeserializer(LocalDate.class, new LocalDateDeserializer(dateFormatter));
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        module.addSerializer(LocalTime.class, new LocalTimeSerializer(timeFormatter));
        module.addDeserializer(LocalTime.class, new LocalTimeDeserializer(timeFormatter));
        return module;
    }

    public static String toJson(Object obj, boolean prettyPrinter, boolean excludeNull) {
        if (null == obj) {
            return "";
        }

        if (obj instanceof CharSequence) {
            return obj.toString();
        }

        ObjectMapper objectMapper;
        if (excludeNull) {
            objectMapper = excludeNullMapper;
        } else {
            objectMapper = useAnnotationMapper;
        }

        try {
            if (prettyPrinter) {
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            } else {
                return objectMapper.writeValueAsString(obj);
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("将%s得对象转换为Json字符串出错,%s", obj.getClass(), e.getCause()), e);
        }
    }

    public static String toJson(Object obj, boolean prettyPrinter) {
        return toJson(obj, prettyPrinter, true);
    }

    public static String toJson(Object obj) {
        return toJson(obj, false);
    }

    @SuppressWarnings("unchecked")
    public static <T> T toPojo(String content, Class<T> resultClazz, Class<?>... parameters) {
        if (resultClazz == String.class) {
            return (T) content;
        }
        return toPojo(content, JavaTypeFactory.constructType(resultClazz, parameters));
    }

    public static <T> T toPojo(String content, JavaType javaType) {
        if (content == null || content.isEmpty()) {
            return null;
        }

        try {
            return useAnnotationMapper.readValue(content, javaType);
        } catch (Exception e) {
            throw new RuntimeException(String.format("将%s解析为%s出错,%s", content, javaType.getRawClass(), e.getCause()), e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T clone(T orgObj) {
        return (T) copy(orgObj, orgObj.getClass());
    }

    public static <T> T copy(Object orgObj, Class<T> descClazz, Class<?>... parameters) {
        return orgObj == null ? null : toPojo(toJson(orgObj), descClazz, parameters);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(Object obj) {
        return toPojo(toJson(obj), Map.class);
    }

}
