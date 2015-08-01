package org.zeroturnaround.javarebel.integration.minecraft.cpb;

import org.zeroturnaround.bundled.javassist.*;
import org.zeroturnaround.javarebel.Logger;
import org.zeroturnaround.javarebel.LoggerFactory;
import org.zeroturnaround.javarebel.integration.minecraft.interfaces.JrBlock;
import org.zeroturnaround.javarebel.integration.minecraft.util.MethodIdentifier;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

/*
  patches net.minecraft.block.Block
 */
public class BlockCPB extends JavassistClassBytecodeProcessor {
  private static Logger log = LoggerFactory.getLogger("Minecraft");
  private static final MethodIdentifier[] REROUTED_METHODS = new MethodIdentifier[]{
      new MethodIdentifier("getBlockHardness"),
      new MethodIdentifier("getCreativeTabToDisplayOn"),
      new MethodIdentifier("getLightOpacity", new CtClass[0]),
      new MethodIdentifier("getLightValue", new CtClass[0]),
      new MethodIdentifier("getMaterial"),
      new MethodIdentifier("getTickRandomly"),
      new MethodIdentifier("getUnlocalizedName"),
      new MethodIdentifier("getUseNeighborBrightness"),
      new MethodIdentifier("isFullBlock"),
      new MethodIdentifier("isPassable"),
      new MethodIdentifier("isTranslucent"),
      new MethodIdentifier("isVisuallyOpaque")
  };

  @Override
  public void process(ClassPool cp, ClassLoader cl, CtClass ctClass) throws Exception {
    cp.importPackage("org.zeroturnaround.javarebel");
    cp.importPackage("org.zeroturnaround.javarebel.integration.minecraft");
    cp.importPackage("org.zeroturnaround.javarebel.integration.util");
    cp.importPackage("net.minecraft.block");

    ctClass.addField(CtField.make("private static Logger _jrLog = LoggerFactory.getLogger(\"Minecraft\");", ctClass));

    configureJrBlockInterface(cp, ctClass);
    configureReroutingMethods(ctClass);
  }


  private void configureJrBlockInterface(ClassPool cp, CtClass ctClass) throws CannotCompileException, NotFoundException {
    ctClass.addField(CtField.make("private Block _jrActiveBlock;", ctClass));

    ctClass.addInterface(cp.get(JrBlock.class.getName()));
    ctClass.addMethod(CtNewMethod.make("" +
        "public void _jrUseBlock(Object block) {" +
        "  _jrActiveBlock = (Block) block;" +
        "}", ctClass));
  }

  private void configureReroutingMethods(CtClass ctClass) {
    for (int i = 0; i < REROUTED_METHODS.length; i++) {
      try {
        CtMethod m = REROUTED_METHODS[i].getDeclaredMethod(ctClass);
        m.insertBefore("" +
            "if (_jrActiveBlock != null && this != _jrActiveBlock) {" +
            "  return _jrActiveBlock." + m.getName() + "($$);" +
            "}");
      } catch (Exception e) {
        log.error("Could not process " + REROUTED_METHODS[i].getMethodName(), e);
      }
    }
  }
}