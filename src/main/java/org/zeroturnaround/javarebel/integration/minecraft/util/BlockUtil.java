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

  public static void rerouteBlock(Object block) {
    ReroutedMethodHandler proxyHandler = (ReroutedMethodHandler) blockInstanceMap.get(block.getClass()).getHandler();
    proxyHandler.setInstance(block);
  }

  public static boolean isReroutedBlockClass(Class klass) {
    return blockInstanceMap.get(klass) != null;
  }

  @SuppressWarnings("unused")
  public static Object getOrCreateProxyBlock(Object block) {
    if (block.getClass().getCanonicalName().contains("net.minecraft")) {
      return block;
    }

    if (block instanceof ProxyObject) {
      return block;
    }
//    new Throwable().printStackTrace();

    Object proxy =  blockInstanceMap.get(block.getClass());
    if (proxy != null) {
      return proxy;
    }

    LoggerFactory.getLogger("Minecraft").infoEcho("creating proxy for {}", new Object[]{block});
    try {
      ReroutedMethodHandler handler = new ReroutedMethodHandler(block);
      ProxyFactory factory = new ProxyFactory();
      factory.setSuperclass(block.getClass());
/*      factory.setFilter(new MethodFilter() {
        @Override
        public boolean isHandled(Method method) {
          return !"toString".equals(method.getName());
        }
      });*/
      Object result = factory.create(new Class<?>[0], new Object[0], handler);
      blockInstanceMap.put(block.getClass(), (ProxyObject) result);
      LoggerFactory.getLogger("Minecraft").infoEcho("created proxy for {}: {}", new Object[]{block, result});
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
    return ((ReroutedMethodHandler) ((ProxyObject) object).getHandler()).getInstance();
  }

  private static class ReroutedMethodHandler implements MethodHandler {
    private Object activeInstance;

    private ReroutedMethodHandler(Object instance) {
      this.activeInstance = instance;
    }

    public void setInstance(Object instance) {
      if (instance instanceof ProxyObject) {
        throw new RuntimeException();
      }
      this.activeInstance = instance;
    }

    public Object getInstance() {
      return activeInstance;
    }

    public Object invoke(Object o, Method method, Method proceed, Object[] args) throws Throwable {
      try {
        return method.invoke(activeInstance, args);
      } catch (InvocationTargetException e) {
        LoggerFactory.getLogger("Minecraft").error("problem invoking proxy mehod: active: {}, object: {}, method: {}, proceed: {}", new Object[]{activeInstance, o, method, proceed});
        LoggerFactory.getLogger("Minecraft").error(e);
        throw new RuntimeException();
        //return method.invoke(o, args);
      }
    }
  }
}