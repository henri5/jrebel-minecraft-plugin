package org.zeroturnaround.javarebel.integration.minecraft.cpb.forge;

import org.zeroturnaround.bundled.javassist.*;
import org.zeroturnaround.bundled.javassist.expr.ExprEditor;
import org.zeroturnaround.bundled.javassist.expr.MethodCall;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

/*
  patches net.minecraftforge.client.model.ModelLoader
 */
public class ModelLoaderCPB extends JavassistClassBytecodeProcessor {
  @Override
  public void process(ClassPool cp, ClassLoader cl, CtClass ctClass) throws Exception {
    cp.importPackage("org.zeroturnaround.javarebel.integration.minecraft.interfaces");

    CtMethod setCustomStateMapper = ctClass.getDeclaredMethod("setCustomStateMapper");
    setCustomStateMapper.instrument(new ExprEditor() {
      public void edit(MethodCall m) throws CannotCompileException {
        if ("put".equals(m.getMethodName())) {
          m.replace(
              "$_ = (net.minecraftforge.fml.common.registry.RegistryDelegate) $proceed(((JrBlock) block)._jrGetDelegate(), mapper);"
          );
        }
      }
    });
  }
}