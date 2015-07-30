package org.zeroturnaround.javarebel.integration.minecraft;

import org.zeroturnaround.javarebel.ClassEventListener;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class BlockClassEventListener implements ClassEventListener {
  public BlockClassEventListener() {}

  public void onClassEvent(int eventType, Class<?> klass) {
    if (BlockUtil.hasClass(klass)) {
      if (eventType == EVENT_RELOADED) {
        Constructor[] ctors = klass.getConstructors();
        Constructor ctor = null;
        for (int i = 0; i < ctors.length; i++) {
          if ((ctor = ctors[i]).getGenericParameterTypes().length == 0) {
            break;
          }
        }
        if (ctor == null) return;

        try {
          ctor.setAccessible(true);
          BlockUtil.rerouteBlock(ctor.newInstance());
        } catch (Exception e) {
          //guess we don't have anything to do
        }
      }
    }
  }

  public int priority() {
    return PRIORITY_DEFAULT;
  }
}
