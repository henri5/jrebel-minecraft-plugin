package org.zeroturnaround.javarebel.integration.minecraft.cpb.forge;

import org.zeroturnaround.bundled.javassist.*;
import org.zeroturnaround.bundled.javassist.expr.ExprEditor;
import org.zeroturnaround.bundled.javassist.expr.FieldAccess;
import org.zeroturnaround.javarebel.integration.minecraft.interfaces.JrBlock;
import org.zeroturnaround.javarebel.integration.minecraft.util.BlockUtil;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

/*
  patches net.minecraftforge.fml.common.registry.GameData
 */
public class GameDataCPB extends JavassistClassBytecodeProcessor {
  @Override
  public void process(ClassPool cp, ClassLoader cl, CtClass ctClass) throws Exception {
    cp.importPackage("net.minecraft.block");
    cp.importPackage("net.minecraftforge.fml.common.registry");
    cp.importPackage("org.zeroturnaround.javarebel.integration.minecraft.util");

    CtMethod registerBlock = ctClass.getDeclaredMethod("registerBlock", new CtClass[]{cp.get("net.minecraft.block.Block"), cp.get("java.lang.String"), CtPrimitiveType.intType});
    registerBlock.insertBefore("" +
        "block = (Block) " + BlockUtil.class.getName() + ".getOrCreateProxyBlock(block);" +
        "");

    registerBlock.instrument(new ExprEditor() {
      public void edit (FieldAccess arg) throws CannotCompileException {
        if ("delegate".equals(arg.getFieldName())) {
          arg.replace("$_ = (RegistryDelegate) ((" + JrBlock.class.getName() + ") block)._jrGetDelegate();");
        }
      }
    });
  }
}