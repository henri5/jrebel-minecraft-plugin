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

public class BlockUtil {
  private static Map<Class, ProxyObject> blockInstanceMap = new HashMap<Class, ProxyObject>();

  public static void updateProxyBlock(Object block) {
    ProxyMethodHandler proxyHandler = getMethodHandlerForClass(block.getClass());
    proxyHandler.setInstance(block);
  }

  public static boolean isProxyBlockClass(Class klass) {
    return blockInstanceMap.get(klass) != null;
  }

  @SuppressWarnings("unused")
  public static Object getOriginalBlock(Object block) {
    if (isProxyBlockClass(block.getClass())) {
      ProxyMethodHandler proxyHandler = getMethodHandlerForClass(block.getClass());
      return proxyHandler.getOriginalInstance();
    }
    return null;
  }

  private static ProxyMethodHandler getMethodHandlerForClass(Class klass) {
    return (ProxyMethodHandler) blockInstanceMap.get(klass).getHandler();
  }

  @SuppressWarnings("unused")
  public static Object getOrCreateProxyBlock(Object block) {
    if (block.getClass().getCanonicalName().contains("net.minecraft")) {
      return block;
    }

    if (block instanceof ProxyObject) {
      return block;
    }

    Object proxy =  blockInstanceMap.get(block.getClass());
    if (proxy != null) {
      return proxy;
    }

    try {
      ProxyMethodHandler handler = new ProxyMethodHandler(block);
      ProxyFactory factory = new ProxyFactory();
      factory.setSuperclass(block.getClass());
      factory.setFilter(new MethodFilter() {
        @Override
        public boolean isHandled(Method method) {
          return !"toString".equals(method.getName());
        }
      });
      Object result = factory.create(new Class<?>[0], new Object[0], handler);
      blockInstanceMap.put(block.getClass(), (ProxyObject) result);
      return result;
    } catch (Exception e) {
      e.printStackTrace();
      //aww dangit
    }
    return block;
  }

  @SuppressWarnings("unused")
  public static Object getRealObjectFor(Object object) {
    if (!(object instanceof ProxyObject)) {
      return object;
    }
    return ((ProxyMethodHandler) ((ProxyObject) object).getHandler()).getInstance();
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