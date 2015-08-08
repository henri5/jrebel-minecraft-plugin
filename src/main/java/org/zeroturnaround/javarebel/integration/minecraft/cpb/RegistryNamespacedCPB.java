package org.zeroturnaround.javarebel.integration.minecraft.cpb;

import org.zeroturnaround.bundled.javassist.ClassPool;
import org.zeroturnaround.bundled.javassist.CtClass;
import org.zeroturnaround.bundled.javassist.CtMethod;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

/*
  patches net.minecraft.util.RegistryNamespaced
 */
public class RegistryNamespacedCPB extends JavassistClassBytecodeProcessor {

  @Override
  public void process(ClassPool cp, ClassLoader cl, CtClass ctClass) throws Exception {
    cp.importPackage("net.minecraft.block");
    cp.importPackage("org.zeroturnaround.javarebel.integration.minecraft.util");


    CtMethod getIDForObject = ctClass.getDeclaredMethod("getIDForObject");
    getIDForObject.insertBefore("" +
        "if ($1 instanceof Block) {" +
        "  $1 = BlockUtil.getOrCreateProxyBlock($1);" +
        "}");

  }
}