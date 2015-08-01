package org.zeroturnaround.javarebel.integration.minecraft.cpb;

import org.zeroturnaround.bundled.javassist.ClassPool;
import org.zeroturnaround.bundled.javassist.CtClass;
import org.zeroturnaround.bundled.javassist.CtField;
import org.zeroturnaround.bundled.javassist.CtMethod;
import org.zeroturnaround.javarebel.integration.minecraft.interfaces.JrMinecraft;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

/*
  patches net.minecraft.client.resources.FileResourcePack
 */
public class FileResourcePackCPB extends JavassistClassBytecodeProcessor {
  @Override
  public void process(ClassPool cp, ClassLoader cl, CtClass ctClass) throws Exception {
    cp.importPackage("java.net");
    cp.importPackage("java.util");
    cp.importPackage("org.zeroturnaround.javarebel");
    cp.importPackage("org.zeroturnaround.javarebel.integration.monitor");
    cp.importPackage("org.zeroturnaround.javarebel.integration.util");
    cp.importPackage("net.minecraft.client");
    cp.importPackage("net.minecraft.client.renderer.texture");
    cp.importPackage("net.minecraft.client.resources");

    ctClass.addField(CtField.make("private static List _jrMonitoredResources = new ArrayList();", ctClass));
    ctClass.addField(CtField.make("private static Logger _jrLog = LoggerFactory.getLogger(\"Minecraft\");", ctClass));

    CtMethod findClass = ctClass.getDeclaredMethod("getInputStreamByName");
    findClass.insertBefore("" +
        "Integration i = IntegrationFactory.getInstance();" +
        "if (i.isResourceReplaced(getClass().getClassLoader(), $1)) {" +
        "  URL url = i.findRebelResource(getClass().getClassLoader(), $1);" +
        "  if (url != null) {" +
        "    if (!_jrMonitoredResources.contains($1)) {" +
        "      _jrMonitoredResources.add($1);" +
        "      " + JrMinecraft.class.getName() + " jrMinecraft = (" + JrMinecraft.class.getName() + ") Minecraft.getMinecraft();" +
        "      jrMinecraft._jrMonitorResource(new MonitoredResource(ResourceUtil.asResource(url)), $0);" +
        "      _jrLog.infoEcho(\"Monitoring resource '{}'\", new Object[]{url.getPath()});" +
        "    }" +
        "    return url.openStream();" +
        "  }" +
        "}");
  }
}