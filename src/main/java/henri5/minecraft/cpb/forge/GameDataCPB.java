package henri5.minecraft.cpb.forge;

import henri5.minecraft.util.ProxyUtil;
import org.zeroturnaround.bundled.javassist.*;
import org.zeroturnaround.bundled.javassist.expr.ExprEditor;
import org.zeroturnaround.bundled.javassist.expr.FieldAccess;
import henri5.minecraft.interfaces.JrBlock;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

/*
  patches net.minecraftforge.fml.common.registry.GameData
 */
public class GameDataCPB extends JavassistClassBytecodeProcessor {
  @Override
  public void process(ClassPool cp, ClassLoader cl, CtClass ctClass) throws Exception {
    cp.importPackage("net.minecraft.block");
    cp.importPackage("net.minecraftforge.fml.common.registry");
    cp.importPackage("henri5.minecraft.util");

    CtMethod registerBlock = ctClass.getDeclaredMethod("registerBlock", new CtClass[]{cp.get("net.minecraft.block.Block"), cp.get("java.lang.String"), CtPrimitiveType.intType});
    registerBlock.insertBefore("" +
        "block = (Block) " + ProxyUtil.class.getName() + ".getOrCreateProxyForObj(block);" +
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