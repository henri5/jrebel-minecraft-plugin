package henri5.minecraft;

import henri5.minecraft.util.ProxyUtil;
import org.zeroturnaround.javarebel.ClassEventListener;

import java.lang.reflect.Constructor;

public class BlockClassEventListener implements ClassEventListener {
  public BlockClassEventListener() {}

  public void onClassEvent(int eventType, Class<?> klass) {
    if (ProxyUtil.isProxiedClass(klass)) {
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
          ProxyUtil.updateProxy(ctor.newInstance());
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
