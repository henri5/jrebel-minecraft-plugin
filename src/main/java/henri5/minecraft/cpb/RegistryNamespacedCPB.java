package henri5.minecraft.cpb;

import henri5.minecraft.util.ProxyUtil;
import org.zeroturnaround.bundled.javassist.ClassPool;
import org.zeroturnaround.bundled.javassist.CtClass;
import org.zeroturnaround.bundled.javassist.CtMethod;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

/*
  patches net.minecraft.util.RegistryNamespaced
 */
public class RegistryNamespacedCPB extends JavassistClassBytecodeProcessor {

  @Override
  public void process(ClassPool cp, ClassLoader cl, CtClass ctClass) throws Exception {
    cp.importPackage("net.minecraft.block");
    cp.importPackage("henri5.minecraft.util");


    CtMethod getIDForObject = ctClass.getDeclaredMethod("getIDForObject");
    getIDForObject.insertBefore("" +
        "if ($1 instanceof Block) {" +
        "  $1 = " + ProxyUtil.class.getName() + ".getOrCreateProxyForObj($1);" +
        "}");

  }
}