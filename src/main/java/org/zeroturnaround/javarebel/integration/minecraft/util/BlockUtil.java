package org.zeroturnaround.javarebel.integration.minecraft.util;

import org.zeroturnaround.javarebel.integration.minecraft.interfaces.JrBlock;

import java.util.HashMap;
import java.util.Map;

public class BlockUtil {
  private static Map<Class, JrBlock> blockMap = new HashMap<Class, JrBlock>();

  public static void rerouteBlock(Object block) {
    JrBlock mainBlock = blockMap.get(block.getClass());
    mainBlock._jrUseBlock(block);
  }

  @SuppressWarnings("unused")
  public static void monitorBlock(JrBlock block) {
    blockMap.put(block.getClass(), block);
  }

  public static boolean isReroutedBlockClass(Class klass) {
    return blockMap.get(klass) != null;
  }
}
