package org.zeroturnaround.javarebel.integration.minecraft.cpb;

import org.zeroturnaround.bundled.javassist.*;
import org.zeroturnaround.javarebel.integration.minecraft.JrSimpleReloadableResourceManager;
import org.zeroturnaround.javarebel.integration.minecraft.JrTextureMap;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

/*
  patches net.minecraft.client.renderer.texture.TextureMap
 */
public class TextureMapCPB extends JavassistClassBytecodeProcessor {
  private static int RELOAD_TRESHOLD = 5000;

  @Override
  public void process(ClassPool cp, ClassLoader cl, CtClass ctClass) throws Exception {
    cp.importPackage("java.util");
    cp.importPackage("org.zeroturnaround.javarebel");
    cp.importPackage("org.zeroturnaround.javarebel.integration.minecraft");
    cp.importPackage("org.zeroturnaround.javarebel.integration.monitor");
    cp.importPackage("org.zeroturnaround.javarebel.integration.util");
    cp.importPackage("net.minecraft.client");
    cp.importPackage("net.minecraft.client.resources");

    ctClass.addField(CtField.make("private java.util.Map _jrResources = new HashMap();", ctClass));
    ctClass.addField(CtField.make("private long _jrLastCheck = 0L;", ctClass));
    ctClass.addField(CtField.make("private static Logger _jrLog = LoggerFactory.getLogger(\"Minecraft\");", ctClass));

    ctClass.addInterface(cp.get(JrTextureMap.class.getName()));

    ctClass.addMethod(CtNewMethod.make("" +
        "public void _jrMonitorResource(MonitoredResource mr, Object iResourcePack) {" +
        "  _jrResources.put(mr, iResourcePack);" +
        "}", ctClass));

    ctClass.addMethod(CtNewMethod.make("" +
        "public void _jrCheckAndReloadResources() {" +
        "  java.util.List reload = new java.util.ArrayList();" +
        "  java.util.Iterator it = _jrResources.entrySet().iterator();" +
        "  while (it.hasNext()) {" +
        "    java.util.Map.Entry pair = (java.util.Map.Entry) it.next();" +
        "    MonitoredResource mr = (MonitoredResource) pair.getKey();" +
        "    if (mr.modified() && !reload.contains(pair.getValue())) {" +
        "      reload.add(pair.getValue());" +
        "    }" +
        "  }" +
        "  if (!reload.isEmpty()) {" +
//        "    System.out.println(\"Reloading resources from \" + reload);" +
        "    SimpleReloadableResourceManager srrm = (SimpleReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();" +
        "    it = reload.iterator();" +
        "    while (it.hasNext()) {" +
        "      IResourcePack iResourcePack = (IResourcePack) it.next();" +
        // can probably replace with Minecraft.getMinecraft().scheduleResourcesRefresh()
        "      srrm.reloadResourcePack(iResourcePack);" +
        "    }" +
        "    ((" + JrSimpleReloadableResourceManager.class.getName() + ") srrm)._jrNotifyReloadListeners();" +
        "  }" +
        "}", ctClass));

    CtMethod tick = ctClass.getDeclaredMethod("tick");
    tick.insertBefore("" +
        "if (_jrLastCheck + " + RELOAD_TRESHOLD + " < System.currentTimeMillis()) {" +
        "  _jrCheckAndReloadResources();" +
        "  _jrLastCheck = System.currentTimeMillis();" +
        "}");

  }
}