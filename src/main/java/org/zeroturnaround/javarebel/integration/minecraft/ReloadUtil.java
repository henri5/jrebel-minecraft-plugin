package org.zeroturnaround.javarebel.integration.minecraft;

import org.zeroturnaround.javarebel.ConfigurationFactory;

import java.util.ArrayList;
import java.util.List;

public class ReloadUtil {
  private static final List<String> ignoreReloadHandlers;
  static {
    ignoreReloadHandlers = new ArrayList<String>();
    String ignoreClassesParameter = ConfigurationFactory.getInstance().getProperty("rebel.minecraft.ignore_reload_handlers");
    if (ignoreClassesParameter != null) {
      String[] ignoreClassesArray = ignoreClassesParameter.split(",");
      for (String klass : ignoreClassesArray) {
        ignoreReloadHandlers.add(klass);
      }
    }
  }

  @SuppressWarnings("unused")
  public static List getIgnoreReloadHandlers() {
    return ignoreReloadHandlers;
  }
}
