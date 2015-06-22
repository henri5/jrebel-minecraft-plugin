package org.zeroturnaround.javarebel.integration.minecraft;

import org.zeroturnaround.javarebel.integration.monitor.MonitoredResource;

public interface JrMinecraft {
  @SuppressWarnings("unused")
  void _jrMonitorResource(MonitoredResource mr, Object /*net.minecraft.client.resources.IResourcePack*/ iResourcePack);
}
