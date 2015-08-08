package org.zeroturnaround.javarebel.integration.minecraft.cpb;

import org.zeroturnaround.bundled.javassist.*;
import org.zeroturnaround.bundled.javassist.expr.ExprEditor;
import org.zeroturnaround.bundled.javassist.expr.MethodCall;
import org.zeroturnaround.javarebel.integration.minecraft.interfaces.JrBlock;
import org.zeroturnaround.javarebel.integration.minecraft.util.BlockUtil;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

/*
  patches net.minecraft.block.Block
 */
public class BlockCPB extends JavassistClassBytecodeProcessor {

  @Override
  public void process(ClassPool cp, ClassLoader cl, CtClass ctClass) throws Exception {
    cp.importPackage("net.minecraft.block");
    cp.importPackage("org.zeroturnaround.javarebel.integration.minecraft.util");

    ctClass.addInterface(cp.get(JrBlock.class.getName()));
    this.getClass().getCanonicalName().contains("$$");
    ctClass.addMethod(CtNewMethod.make("" +
        "public Object _jrGetDelegate() {" +
        "  return delegate;" +
        "}", ctClass));

    CtConstructor constructor = ctClass.getDeclaredConstructor(new CtClass[]{cp.get("net.minecraft.block.material.Material")});
    constructor.instrument(new ExprEditor() {
      public void edit(MethodCall m) throws CannotCompileException {
        if ("createBlockState".equals(m.getMethodName())) {
          m.replace(
               "if (!this.getClass().getCanonicalName().contains(\"_$$_\")) {" +
               "  $_ = $proceed($$);" +
               "} else {" +
               "  return;" +
               "}"
          );
        }
      }
    });

    CtMethod getLightValue = ctClass.getDeclaredMethod("getLightValue", new CtClass[]{cp.get("net.minecraft.world.IBlockAccess"),cp.get("net.minecraft.util.BlockPos")});
    getLightValue.instrument(new ExprEditor() {
      public void edit(MethodCall m) throws CannotCompileException {
        if ("getBlock".equals(m.getMethodName())) {
          m.replace(
               "$_ = $proceed($$);" +
               "$_ = (Block) " + BlockUtil.class.getName() + ".getRealObjectFor($_);"
          );
        }
      }
    });

  }
}