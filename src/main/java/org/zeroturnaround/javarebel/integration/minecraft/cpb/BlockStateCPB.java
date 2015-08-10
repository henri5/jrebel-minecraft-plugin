package org.zeroturnaround.javarebel.integration.minecraft.cpb;

import org.zeroturnaround.bundled.javassist.ClassPool;
import org.zeroturnaround.bundled.javassist.CtClass;
import org.zeroturnaround.bundled.javassist.CtConstructor;
import org.zeroturnaround.javarebel.integration.minecraft.util.BlockUtil;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

/*
  patches net.minecraft.block.state.BlockState
 */
public class BlockStateCPB extends JavassistClassBytecodeProcessor {
  @Override
  public void process(ClassPool cp, ClassLoader cl, CtClass ctClass) throws Exception {
    cp.importPackage("org.zeroturnaround.javarebel.integration.minecraft.util");
    cp.importPackage("net.minecraft.block");

    CtConstructor constructor = ctClass.getDeclaredConstructor(
        new CtClass[]{
            cp.get("net.minecraft.block.Block"),
            cp.get("net.minecraft.block.properties.IProperty[]"),
            cp.get("com.google.common.collect.ImmutableMap")
        });
    constructor.insertBefore("" +
        "blockIn = (Block) " + BlockUtil.class.getName() + ".getOrCreateProxyBlock($1);" +
        "");
  }
}