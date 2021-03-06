package henri5.minecraft.cpb;

import org.zeroturnaround.bundled.javassist.ClassPool;
import org.zeroturnaround.bundled.javassist.CtClass;
import org.zeroturnaround.bundled.javassist.CtConstructor;
import org.zeroturnaround.bundled.javassist.CtMethod;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

/*
  patches net.minecraft.launchwrapper.LaunchClassLoader
 */
public class LauncherClassLoaderCBP extends JavassistClassBytecodeProcessor {
  @Override
  public void process(ClassPool cp, ClassLoader cl, CtClass ctClass) throws Exception {
    cp.importPackage("org.zeroturnaround.javarebel");
    cp.importPackage("org.zeroturnaround.javarebel.integration.util");

    CtConstructor[] cs = ctClass.getConstructors();
    for (int i = 0; i < cs.length; i++) {
      cs[i].insertAfter("addTransformerExclusion(\"com.zeroturnaround.javarebel.gen.RebelLocator$$\");");
    }

    CtMethod findClass = ctClass.getDeclaredMethod("findClass");
    findClass.insertBefore("" +
        "Class result = IntegrationFactory.getInstance().findReloadableClass(this, $1);" +
        "if (result != null) return result;");

  }
}