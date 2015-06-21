package org.zeroturnaround.javarebel.integration.minecraft.cpb;

import org.zeroturnaround.bundled.javassist.*;
import org.zeroturnaround.bundled.javassist.expr.ExprEditor;
import org.zeroturnaround.bundled.javassist.expr.MethodCall;
import org.zeroturnaround.javarebel.integration.minecraft.JrSimpleReloadableResourceManager;
import org.zeroturnaround.javarebel.integration.minecraft.ReloadUtil;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

import java.util.List;

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
        "public void _jrNotifyReloadListeners() {" +
        "  notifyReloadListeners();" +
        "}", ctClass));
    CtMethod registerReloadListener = ctClass.getDeclaredMethod("registerReloadListener");
    registerReloadListener.insertBefore("" +
        "System.out.println($1);" +
        "new Throwable().printStackTrace();");

    CtMethod notifyReloadListeners = ctClass.getDeclaredMethod("notifyReloadListeners");
    notifyReloadListeners.instrument(new ExprEditor(){
      @Override
      public void edit(MethodCall m) throws CannotCompileException {
        if ("onResourceManagerReload".equals(m.getMethodName())) {
          m.replace("{" +
              "if (" + ReloadUtil.class.getName() + ".getIgnoreReloadHandlers().contains(iresourcemanagerreloadlistener.getClass().getName())) {" +
              "  System.out.println(\"Skipping \" + iresourcemanagerreloadlistener);" +
              "} else {" +
              "  long startTime = System.currentTimeMillis();" +
              "  $proceed($$);" +
              "  System.out.println(\"Method invocation took \" + (System.currentTimeMillis() - startTime) + \" for \" + iresourcemanagerreloadlistener);" +
              "  }" +
              "}");
        }
        super.edit(m);
      }
    });
  }
}