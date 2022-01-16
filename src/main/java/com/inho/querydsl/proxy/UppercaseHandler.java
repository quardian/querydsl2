package com.inho.querydsl.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Locale;

public class UppercaseHandler implements InvocationHandler {
    Object target;

    public UppercaseHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        Object ret = method.invoke(target, objects);

        String methodName = method.getName();

        if ( ret instanceof String && methodName.startsWith("say") ){
            return ((String)ret).toUpperCase();
        }
        return ret;
    }
}
