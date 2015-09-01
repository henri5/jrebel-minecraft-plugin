package org.zeroturnaround.javarebel.integration.minecraft.util;

import org.zeroturnaround.bundled.javassist.util.proxy.MethodHandler;
import org.zeroturnaround.bundled.javassist.util.proxy.ProxyObject;
import org.zeroturnaround.javarebel.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ProxyMethodHandler implements MethodHandler {
    private Object activeInstance;
    private final Object originalInstance;

    public ProxyMethodHandler(Object instance) {
        this.activeInstance = instance;
        this.originalInstance = instance;
    }

    public void setInstance(Object instance) {
        if (instance instanceof ProxyObject) {
            throw new RuntimeException("Error updating the instance in proxy: " + instance + " is a " + ProxyObject.class.getName());
        }
        this.activeInstance = instance;
    }

    public Object getOriginalInstance() {
        return originalInstance;
    }

    public Object getInstance() {
        return activeInstance;
    }

    public Object invoke(Object o, Method method, Method proceed, Object[] args) throws Throwable {
        try {
            return method.invoke(activeInstance, args);
        } catch (InvocationTargetException e) {
            LoggerFactory.getLogger("Minecraft").error("problem invoking proxy method: active: {}, object: {}, method: {}, proceed: {}", new Object[]{activeInstance, o, method, proceed});
            throw new RuntimeException(e);
        }
    }
}
