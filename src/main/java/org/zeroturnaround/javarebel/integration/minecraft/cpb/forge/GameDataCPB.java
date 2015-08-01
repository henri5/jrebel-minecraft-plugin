package org.zeroturnaround.javarebel.integration.minecraft.cpb.forge;

import org.zeroturnaround.bundled.javassist.*;
import org.zeroturnaround.javarebel.integration.minecraft.interfaces.JrBlock;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

/*
  patches net.minecraftforge.fml.common.registry.GameData
 */
public class GameDataCPB extends JavassistClassBytecodeProcessor {
  @Override
  public void process(ClassPool cp, ClassLoader cl, CtClass ctClass) throws Exception {
    cp.importPackage("org.zeroturnaround.javarebel.integration.minecraft.interfaces");
    cp.importPackage("org.zeroturnaround.javarebel.integration.minecraft.util");

    CtMethod findClass = ctClass.getDeclaredMethod("registerBlock", new CtClass[]{cp.get("net.minecraft.block.Block"), cp.get("java.lang.String"), CtPrimitiveType.intType});
    findClass.insertBefore("BlockUtil.monitorBlock((" + JrBlock.class.getName() + ") block);");
  }
}