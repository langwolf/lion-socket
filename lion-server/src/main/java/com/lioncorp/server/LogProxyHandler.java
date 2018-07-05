package com.lioncorp.server;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

import com.lioncorp.common.util.LoggerUtility;

public class LogProxyHandler<T> implements InvocationHandler {

    private T instance;

    public LogProxyHandler(T instance) {
        this.instance = instance;
    }

    public Object createProxy() {
        return Proxy.newProxyInstance(instance.getClass().getClassLoader(), instance.getClass().getInterfaces(), this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        LoggerUtility.beforeInvoke();
        try {
            Long current_time = System.currentTimeMillis();
            LoggerUtility.noticeLog("invoke method is %s param is %s \r\n", method.getName(), Arrays.toString(args));
            Object res = method.invoke(instance, args);
            LoggerUtility.noticeLog("cost time is %s \r\n", System.currentTimeMillis() - current_time);
            LoggerUtility.returnInvoke(instance.getClass());
            return res;
        } catch (Throwable e) {
            LoggerUtility.throwableInvoke(proxy.getClass(), "[result = exception: {%s}", e.getMessage());
            throw e;
        }
    }
}
