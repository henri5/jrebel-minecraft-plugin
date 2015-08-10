package org.zeroturnaround.javarebel.integration.minecraft.util;

import org.zeroturnaround.bundled.javassist.util.proxy.MethodFilter;
import org.zeroturnaround.bundled.javassist.util.proxy.MethodHandler;
import org.zeroturnaround.bundled.javassist.util.proxy.ProxyFactory;
import org.zeroturnaround.bundled.javassist.util.proxy.ProxyObject;
import org.zeroturnaround.javarebel.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ProxyUtil {
  private static Map<Class, ProxyObject> proxyInstanceMap = new HashMap<Class, ProxyObject>();

  public static void updateProxy(Object obj) {
    ProxyMethodHandler proxyHandler = getMethodHandlerForClass(obj.getClass());
    proxyHandler.setInstance(obj);
  }

  public static boolean isProxiedClass(Class klass) {
    return proxyInstanceMap.get(klass) != null;
  }

  @SuppressWarnings("unused")
  public static Object getOriginalObj(Object obj) {
    if (isProxiedClass(obj.getClass())) {
      ProxyMethodHandler proxyHandler = getMethodHandlerForClass(obj.getClass());
      return proxyHandler.getOriginalInstance();
    }
    return null;
  }

  private static ProxyMethodHandler getMethodHandlerForClass(Class klass) {
    return (ProxyMethodHandler) proxyInstanceMap.get(klass).getHandler();
  }

  @SuppressWarnings("unused")
  public static Object getOrCreateProxyForObj(Object obj) {
    if (obj.getClass().getCanonicalName().contains("net.minecraft")) {
      return obj;
    }

    if (obj instanceof ProxyObject) {
      return obj;
    }

    Object proxy =  proxyInstanceMap.get(obj.getClass());
    if (proxy != null) {
      return proxy;
    }

    try {
      ProxyMethodHandler handler = new ProxyMethodHandler(obj);
      ProxyFactory factory = new ProxyFactory();
      factory.setSuperclass(obj.getClass());
      factory.setFilter(new MethodFilter() {
        @Override
        public boolean isHandled(Method method) {
          return !"toString".equals(method.getName());
        }
      });
      Object result = factory.create(new Class<?>[0], new Object[0], handler);
      proxyInstanceMap.put(obj.getClass(), (ProxyObject) result);
      return result;
    } catch (Exception e) {
      e.printStackTrace();
      //aww dangit
    }
    return obj;
  }

  @SuppressWarnings("unused")
  public static Object getRealObjectFor(Object obj) {
    if (!(obj instanceof ProxyObject)) {
      return obj;
    }
    return ((ProxyMethodHandler) ((ProxyObject) obj).getHandler()).getInstance();
  }

  private static class ProxyMethodHandler implements MethodHandler {
    private Object activeInstance;
    private final Object originalInstance;

    private ProxyMethodHandler(Object instance) {
      this.activeInstance = instance;
      this.originalInstance = instance;
    }

    private void setInstance(Object instance) {
      if (instance instanceof ProxyObject) {
        throw new RuntimeException();
      }
      this.activeInstance = instance;
    }

    private Object getOriginalInstance() {
      return originalInstance;
    }

    public Object getInstance() {
      return activeInstance;
    }

    public Object invoke(Object o, Method method, Method proceed, Object[] args) throws Throwable {
      try {
        return method.invoke(activeInstance, args);
      } catch (InvocationTargetException e) {
        LoggerFactory.getLogger("Minecraft").error("problem invoking proxy mehod: active: {}, object: {}, method: {}, proceed: {}", new Object[]{activeInstance, o, method, proceed});
        throw new RuntimeException(e);
      }
    }
  }
}