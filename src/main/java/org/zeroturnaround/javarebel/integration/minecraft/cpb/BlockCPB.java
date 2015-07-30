package org.zeroturnaround.javarebel.integration.minecraft.cpb;

import org.zeroturnaround.bundled.javassist.*;
import org.zeroturnaround.javarebel.integration.minecraft.JrBlock;
import org.zeroturnaround.javarebel.integration.minecraft.JrMinecraft;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

/*
  patches net.minecraft.block.Block
 */
public class BlockCPB extends JavassistClassBytecodeProcessor {
  @Override
  public void process(ClassPool cp, ClassLoader cl, CtClass ctClass) throws Exception {
    cp.importPackage("org.zeroturnaround.javarebel");
    cp.importPackage("org.zeroturnaround.javarebel.integration.minecraft");
    cp.importPackage("org.zeroturnaround.javarebel.integration.util");
    cp.importPackage("net.minecraft.block");

    ctClass.addField(CtField.make("private static Logger _jrLog = LoggerFactory.getLogger(\"Minecraft\");", ctClass));

    CtMethod findClass = ctClass.getDeclaredMethod("getBlockHardness");
    findClass.insertBefore("" +
        "Block block = (Block) BlockUtil.getBlock(this.getClass());" +
        "if (block != null && this != block) {" +
        "  return block.getBlockHardness($$);" +
        "}");
  }
}