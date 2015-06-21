package org.zeroturnaround.javarebel.integration.minecraft.cpb;

import org.zeroturnaround.bundled.javassist.ClassPool;
import org.zeroturnaround.bundled.javassist.CtClass;
import org.zeroturnaround.bundled.javassist.CtField;
import org.zeroturnaround.bundled.javassist.CtMethod;
import org.zeroturnaround.javarebel.integration.minecraft.JrTextureMap;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

/*
  patches net.minecraft.client.resources.FileResourcePack
 */
public class FileResourcePackCPB extends JavassistClassBytecodeProcessor {
  @Override
  public void process(ClassPool cp, ClassLoader cl, CtClass ctClass) throws Exception {
    cp.importPackage("java.util");
    cp.importPackage("org.zeroturnaround.javarebel");
    cp.importPackage("org.zeroturnaround.javarebel.integration.monitor");
    cp.importPackage("org.zeroturnaround.javarebel.integration.util");
    cp.importPackage("net.minecraft.client");
    cp.importPackage("net.minecraft.client.renderer.texture");
    cp.importPackage("net.minecraft.client.resources");

    ctClass.addField(CtField.make("public static List _jrMonitoredResources = new ArrayList();", ctClass));
    ctClass.addField(CtField.make("private static Logger _jrLog = LoggerFactory.getLogger(\"Minecraft\");", ctClass));

    CtMethod findClass = ctClass.getDeclaredMethod("getInputStreamByName");
    findClass.insertBefore("" +
        "Integration i = IntegrationFactory.getInstance();" +
        "if (i.isResourceReplaced(getClass().getClassLoader(), $1)) {" +
        "  java.net.URL url = i.findRebelResource(getClass().getClassLoader(), $1);" +
        "  if (url != null) {" +
//        "    System.out.println(url.getPath());" +
//        "    new Throwable().printStackTrace();" +
        "    if (!_jrMonitoredResources.contains($1)) {" +
        "      _jrMonitoredResources.add($1);" +
//        "      System.out.println($0);" +
        "      " + JrTextureMap.class.getName() + " jrTextureMap = (" + JrTextureMap.class.getName() + ") Minecraft.getMinecraft().getTextureMapBlocks();" +
        "      jrTextureMap._jrMonitorResource(new MonitoredResource(ResourceUtil.asResource(url)), $0);" +
        "      _jrLog.infoEcho(\"Monitoring resource '\" + url.getPath() +\"'\");" +
        "    }" +
        "    return url.openStream();" +
        "  }" +
        "}");

  }
}