package henri5.minecraft.cpb.forge;

import org.zeroturnaround.bundled.javassist.CannotCompileException;
import org.zeroturnaround.bundled.javassist.ClassPool;
import org.zeroturnaround.bundled.javassist.CtClass;
import org.zeroturnaround.bundled.javassist.CtMethod;
import org.zeroturnaround.bundled.javassist.expr.ExprEditor;
import org.zeroturnaround.bundled.javassist.expr.MethodCall;
import henri5.minecraft.interfaces.JrBlock;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

/*
  patches net.minecraftforge.client.model.ModelLoader
 */
public class ModelLoaderCPB extends JavassistClassBytecodeProcessor {
  @Override
  public void process(ClassPool cp, ClassLoader cl, CtClass ctClass) throws Exception {
    cp.importPackage("net.minecraftforge.fml.common.registry");
    cp.importPackage("henri5.minecraft.interfaces");

    CtMethod setCustomStateMapper = ctClass.getDeclaredMethod("setCustomStateMapper");
    setCustomStateMapper.instrument(new ExprEditor() {
      public void edit(MethodCall m) throws CannotCompileException {
        if ("put".equals(m.getMethodName())) {
          m.replace(
              "$_ = (RegistryDelegate) $proceed(((" + JrBlock.class.getName() +  ") block)._jrGetDelegate(), mapper);"
          );
        }
      }
    });
  }
}