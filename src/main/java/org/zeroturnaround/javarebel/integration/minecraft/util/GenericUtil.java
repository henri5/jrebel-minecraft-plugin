package org.zeroturnaround.javarebel.integration.minecraft.util;


import java.lang.reflect.Constructor;

public class GenericUtil {
  public static Constructor getNoParamCtorOrNull(Class klass) {
    Constructor[] ctors = klass.getConstructors();
    Constructor ctor = null;
    for (int i = 0; i < ctors.length; i++) {
      if ((ctor = ctors[i]).getGenericParameterTypes().length == 0) {
        break;
      }
    }
    return ctor;
  }
}
