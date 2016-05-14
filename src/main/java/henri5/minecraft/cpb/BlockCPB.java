package henri5.minecraft.cpb;

import henri5.minecraft.interfaces.JrBlock;
import henri5.minecraft.util.ProxyUtil;
import org.zeroturnaround.bundled.javassist.*;
import org.zeroturnaround.bundled.javassist.expr.ExprEditor;
import org.zeroturnaround.bundled.javassist.expr.MethodCall;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

/*
  patches net.minecraft.block.Block
 */
public class BlockCPB extends JavassistClassBytecodeProcessor {

  @Override
  public void process(ClassPool cp, ClassLoader cl, CtClass ctClass) throws Exception {
    cp.importPackage("net.minecraft.block");
    cp.importPackage("henri5.minecraft.util");

    ctClass.addInterface(cp.get(JrBlock.class.getName()));

    ctClass.addMethod(CtNewMethod.make("" +
        "public Object _jrGetDelegate() {" +
        "  return delegate;" +
        "}", ctClass));

    CtMethod getLightValue = ctClass.getDeclaredMethod("getLightValue", new CtClass[]{cp.get("net.minecraft.world.IBlockAccess"),cp.get("net.minecraft.util.BlockPos")});
    getLightValue.instrument(new ExprEditor() {
      public void edit(MethodCall m) throws CannotCompileException {
        if ("getBlock".equals(m.getMethodName())) {
          m.replace(
               "$_ = $proceed($$);" +
               "$_ = (Block) " + ProxyUtil.class.getName() + ".getRealObjectFor($_);"
          );
        }
      }
    });

    CtMethod createBlockState = ctClass.getDeclaredMethod("createBlockState");
    createBlockState.insertBefore("" +
        "Block block = (Block) " + ProxyUtil.class.getName() + ".getOriginalObj(this);" +
        "if (block != null) {" +
        "  return block.blockState;" +
        "}");

  }
}