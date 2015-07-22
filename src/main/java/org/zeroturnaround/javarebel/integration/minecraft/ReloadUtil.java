package org.zeroturnaround.javarebel.integration.minecraft;

import org.zeroturnaround.javarebel.ConfigurationFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReloadUtil {
  private static final List<String> skippedReloadHandlers;
  static {
    String skipClassesParameter = ConfigurationFactory.getInstance().getProperty("rebel.minecraft.skip_reload_handlers");
    if (skipClassesParameter != null) {
      skippedReloadHandlers = Arrays.asList(skipClassesParameter.split(","));
    } else {
      skippedReloadHandlers = new ArrayList<String>();
    }
  }

  @SuppressWarnings("unused")
  public static boolean runReloadHandler(Class klass) {
    if (skippedReloadHandlers.contains(klass.getName())) {
      return false;
    }
    return true;
  }
}
