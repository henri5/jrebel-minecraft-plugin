package org.zeroturnaround.javarebel.integration.minecraft;

import java.util.HashMap;
import java.util.Map;

public class BlockUtil {
  private static Map<Class, Object> blockMap = new HashMap<Class, Object>();

  public static void rerouteBlock(Object block) {
    blockMap.put(block.getClass(), block);
  }

  public static Object getBlock(Class klass) {
    return blockMap.get(klass);
  }

  public static boolean hasClass(Class klass) {
    return getBlock(klass) != null;
  }
}
