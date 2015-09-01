package org.zeroturnaround.javarebel.integration.minecraft.util;

import org.zeroturnaround.bundled.javassist.util.proxy.MethodFilter;
import org.zeroturnaround.bundled.javassist.util.proxy.Proxy;
import org.zeroturnaround.bundled.javassist.util.proxy.ProxyFactory;
import org.zeroturnaround.bundled.javassist.util.proxy.ProxyObject;
import org.zeroturnaround.javarebel.Logger;
import org.zeroturnaround.javarebel.LoggerFactory;

import java.lang.reflect.Constructor;
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
    return getProxyObject(klass) != null;
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
    return (ProxyMethodHandler) getProxyObject(klass).getHandler();
  }

  private static ProxyObject getProxyObject(Class klass) {
    return proxyInstanceMap.get(klass);
  }

  @SuppressWarnings("unused")
  public static Object getOrCreateProxyForObj(Object obj) {
    if (isNonhandledObject(obj)) {
      return obj;
    }

    Object proxy = getProxyObject(obj.getClass());
    if (proxy != null) {
      return proxy;
    }

    Logger log = LoggerFactory.getLogger("Minecraft");
    log.info("creating proxy for {}", obj);

    try {
      Constructor ctor = getProxyClassConstructor(obj);
      if (ctor == null) {
        //no-param ctor was not found, we cannot create any proxies for it
        //as we don't know the necessary arguments
        return obj;
      }
      Object result = ctor.newInstance(new Object[0]);

      ProxyMethodHandler handler = new ProxyMethodHandler(obj);
      ((Proxy) result).setHandler(handler);

      proxyInstanceMap.put(obj.getClass(), (ProxyObject) result);
      log.info("created proxy for {}: {}", new Object[]{obj, result});
      return result;
    } catch (Exception e) {
      log.error("Problem creating proxy for " + obj, e);
      //aww dangit
    }
    return obj;
  }

  private static boolean isNonhandledObject(Object obj) {
    if (obj.getClass().getCanonicalName().contains("net.minecraft")) {
      //we can't really change the code for default objects anyway
      //hence no point to proxy those
      return true;
    }
    if (obj instanceof ProxyObject) {
      return true;
    }
    return false;
  }

  private static Constructor getProxyClassConstructor(Object obj) {
    Class proxyClass = createProxyClass(obj);
    return GenericUtil.getNoParamCtorOrNull(proxyClass);
  }

  private static Class createProxyClass(Object obj) {
    ProxyFactory factory = new ProxyFactory();
    factory.setSuperclass(obj.getClass());
    factory.setFilter(new MethodFilter() {
      @Override
      public boolean isHandled(Method method) {
        return !"toString".equals(method.getName());
      }
    });
    return factory.createClass();
  }

  @SuppressWarnings("unused")
  public static Object getRealObjectFor(Object obj) {
    if (!(obj instanceof ProxyObject)) {
      return obj;
    }
    return ((ProxyMethodHandler) ((ProxyObject) obj).getHandler()).getInstance();
  }
}