package org.zeroturnaround.javarebel.integration.minecraft.util;


import java.lang.reflect.Constructor;

public class GenericUtil {
  public static Constructor getNoParamCtorOrNull(Class klass) {
    for (Constructor ctor : klass.getConstructors()) {
      if (ctor.getGenericParameterTypes().length == 0) {
        return ctor;
      }
    }
    return null;
  }
}
