package com.platon.browser.util;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

public final class JavaTypeFactory {
    private JavaTypeFactory() {
    }


    public static JavaType constructType(Type clazz) {
        return TypeFactory.defaultInstance().constructType(clazz);
    }

    public static JavaType constructType(Class<?> clazz, Class<?>... parameters) {
        if (parameters == null) {
            return TypeFactory.defaultInstance().constructType(clazz);
        }

        ArrayDeque<Class<?>> arrayDeque = new ArrayDeque<>();
        arrayDeque.add(clazz);
        arrayDeque.addAll(Arrays.asList(parameters));
        return constructType(arrayDeque);
    }


    public static JavaType constructType(List<Class<?>> clazzList) {
        return constructType(new ArrayDeque<>(clazzList));
    }


    private static JavaType constructType(Queue<Class<?>> classQueue) {
        Class<?> clazz = classQueue.poll();
        if (clazz.getTypeParameters().length == 0 || classQueue.size() == 0) {
            return TypeFactory.defaultInstance().constructType(clazz);
        }

        JavaType[] typeParameters = new JavaType[clazz.getTypeParameters().length];

        for (int index = 0; index < typeParameters.length; ++index) {
            typeParameters[index] = constructType(classQueue);
        }

        return TypeFactory.defaultInstance().constructParametricType(clazz, typeParameters);
    }

}
