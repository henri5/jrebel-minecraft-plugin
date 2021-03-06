package henri5.minecraft.cpb;

import henri5.minecraft.interfaces.JrMinecraft;
import org.zeroturnaround.bundled.javassist.*;
import henri5.minecraft.interfaces.JrSimpleReloadableResourceManager;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

/*
  patches net.minecraft.client.Minecraft
 */
public class MinecraftCPB extends JavassistClassBytecodeProcessor {
  private static int RELOAD_TRESHOLD = 5000;

  @Override
  public void process(ClassPool cp, ClassLoader cl, CtClass ctClass) throws Exception {
    cp.importPackage("java.util");
    cp.importPackage("net.minecraft.client");
    cp.importPackage("net.minecraft.client.resources");
    cp.importPackage("org.zeroturnaround.javarebel");
    cp.importPackage("henri5.minecraft.interfaces");
    cp.importPackage("org.zeroturnaround.javarebel.integration.monitor");
    cp.importPackage("org.zeroturnaround.javarebel.integration.util");

    ctClass.addField(CtField.make("private long _jrLastCheck = 0L;", ctClass));
    ctClass.addField(CtField.make("private Map _jrResources = new HashMap();", ctClass));

    ctClass.addInterface(cp.get(JrMinecraft.class.getName()));

    ctClass.addMethod(CtNewMethod.make("" +
        "public void _jrMonitorResource(MonitoredResource mr, Object iResourcePack) {" +
        "  _jrResources.put(mr, iResourcePack);" +
        "}", ctClass));

    ctClass.addMethod(CtNewMethod.make("" +
        "public void _jrCheckAndReloadResources() {" +
        "  List changedResourcePacks = new ArrayList();" +
        "  Iterator it = _jrResources.entrySet().iterator();" +
        "  while (it.hasNext()) {" +
        "    java.util.Map.Entry pair = (java.util.Map.Entry) it.next();" +
        "    MonitoredResource mr = (MonitoredResource) pair.getKey();" +
        "    if (mr.modified() && !changedResourcePacks.contains(pair.getValue())) {" +
        "      changedResourcePacks.add(pair.getValue());" +
        "    }" +
        "  }" +
        "  if (!changedResourcePacks.isEmpty()) {" +
        "    SimpleReloadableResourceManager srrm = (SimpleReloadableResourceManager) getResourceManager();" +
        "    it = changedResourcePacks.iterator();" +
        "    while (it.hasNext()) {" +
        "      IResourcePack resourcePack = (IResourcePack) it.next();" +
        // can probably replace with Minecraft.getMinecraft().scheduleResourcesRefresh()
        "      srrm.reloadResourcePack(resourcePack);" +
        "    }" +
        "    ((" + JrSimpleReloadableResourceManager.class.getName() + ") srrm)._jrNotifyReloadListeners();" +
        "  }" +
        "}", ctClass));

    CtMethod runTick = ctClass.getDeclaredMethod("runTick");
    runTick.insertBefore("" +
        "if (_jrLastCheck + " + RELOAD_TRESHOLD + " < System.currentTimeMillis()) {" +
        "  _jrCheckAndReloadResources();" +
        "  _jrLastCheck = System.currentTimeMillis();" +
        "}");

  }
}