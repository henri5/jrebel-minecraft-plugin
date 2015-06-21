package org.zeroturnaround.javarebel.integration.minecraft.cpb;

import org.zeroturnaround.bundled.javassist.ClassPool;
import org.zeroturnaround.bundled.javassist.CtClass;
import org.zeroturnaround.bundled.javassist.CtNewMethod;
import org.zeroturnaround.javarebel.integration.minecraft.JrSimpleReloadableResourceManager;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

/*
  patches net.minecraft.client.resources.SimpleReloadableResourceManager
 */
public class SimpleReloadableResourceManagerCBP extends JavassistClassBytecodeProcessor {
  @Override
  public void process(ClassPool cp, ClassLoader cl, CtClass ctClass) throws Exception {
    cp.importPackage("org.zeroturnaround.javarebel");
    cp.importPackage("org.zeroturnaround.javarebel.integration.util");

    ctClass.addInterface(cp.get(JrSimpleReloadableResourceManager.class.getName()));

    ctClass.addMethod(CtNewMethod.make("" +
        "public void _jrNotifyListeners() {" +
        "  notifyReloadListeners();" +
        "}", ctClass));
  }
}