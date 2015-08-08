package org.zeroturnaround.javarebel.integration.minecraft.cpb;

import org.zeroturnaround.bundled.javassist.*;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

/*
  patches net.minecraft.block.state.BlockState
 */
public class BlockStateCPB extends JavassistClassBytecodeProcessor {
  @Override
  public void process(ClassPool cp, ClassLoader cl, CtClass ctClass) throws Exception {
    cp.importPackage("org.zeroturnaround.javarebel");
    cp.importPackage("org.zeroturnaround.javarebel.integration.util");

    cp.importPackage("org.zeroturnaround.javarebel.integration.minecraft.interfaces");
    cp.importPackage("org.zeroturnaround.javarebel.integration.minecraft.util");
    cp.importPackage("net.minecraft.block");
    cp.importPackage("net.minecraft.block.material");
    ctClass.addField(CtField.make("private static Logger _jrLog = LoggerFactory.getLogger(\"Minecraft\");", ctClass));

    CtConstructor constructor = ctClass.getDeclaredConstructor(new CtClass[]{cp.get("net.minecraft.block.Block"), cp.get("net.minecraft.block.properties.IProperty[]"), cp.get("com.google.common.collect.ImmutableMap")});
    constructor.insertBefore("" +
        "blockIn = (Block) BlockUtil.getOrCreateProxyBlock(blockIn);" +
        "");
    constructor.insertAfter("" +
        "java.util.Iterator it = validStates.iterator();" +
        "while (it.hasNext() && !this.block.getClass().getCanonicalName().contains(\"net.minecraft\")) {" +
        "  net.minecraft.block.state.IBlockState blockState = (net.minecraft.block.state.IBlockState) it.next();" +
        "}");

    CtMethod registerBlock = ctClass.getDeclaredMethod("getValidStates");
    registerBlock.insertBefore("" +
        "java.util.Iterator it = validStates.iterator();" +
        "while (it.hasNext() && !this.block.getClass().getCanonicalName().contains(\"net.minecraft\")) {" +
        "  net.minecraft.block.state.IBlockState blockState = (net.minecraft.block.state.IBlockState) it.next();" +
        "}");
  }
}