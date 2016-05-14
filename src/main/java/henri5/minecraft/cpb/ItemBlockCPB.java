package henri5.minecraft.cpb;

import henri5.minecraft.util.ProxyUtil;
import org.zeroturnaround.bundled.javassist.ClassPool;
import org.zeroturnaround.bundled.javassist.CtClass;
import org.zeroturnaround.bundled.javassist.CtConstructor;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

/*
  patches net.minecraft.item.ItemBlock
 */
public class ItemBlockCPB extends JavassistClassBytecodeProcessor {

  @Override
  public void process(ClassPool cp, ClassLoader cl, CtClass ctClass) throws Exception {
    cp.importPackage("net.minecraft.block");
    cp.importPackage("henri5.minecraft.util");

    CtConstructor constructor = ctClass.getDeclaredConstructor(new CtClass[]{cp.get("net.minecraft.block.Block")});
    constructor.insertBefore("" +
        "$1 = (Block) " + ProxyUtil.class.getName() + ".getOrCreateProxyForObj($1);" +
        "");
  }
}