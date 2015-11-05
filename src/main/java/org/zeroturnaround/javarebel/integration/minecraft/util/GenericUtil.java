package org.zeroturnaround.javarebel.integration.minecraft.util;


import java.lang.reflect.Constructor;

public class GenericUtil {
  public static Constructor getNoParamCtorOrNull(Class klass) {
    Constructor ctor = null;
    for (Constructor c : klass.getConstructors()) {
      if (c.getGenericParameterTypes().length == 0) {
        ctor = c;
        break;
      }
    }
    return ctor;
  }
}
