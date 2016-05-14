package henri5.minecraft.cpb;

import org.zeroturnaround.bundled.javassist.*;
import org.zeroturnaround.bundled.javassist.expr.ExprEditor;
import org.zeroturnaround.bundled.javassist.expr.MethodCall;
import henri5.minecraft.interfaces.JrSimpleReloadableResourceManager;
import henri5.minecraft.util.ReloadUtil;
import org.zeroturnaround.javarebel.integration.support.JavassistClassBytecodeProcessor;

/*
  patches net.minecraft.client.resources.SimpleReloadableResourceManager
 */
public class SimpleReloadableResourceManagerCBP extends JavassistClassBytecodeProcessor {
  @Override
  public void process(ClassPool cp, ClassLoader cl, CtClass ctClass) throws Exception {
    cp.importPackage("org.zeroturnaround.javarebel");
    cp.importPackage("henri5.minecraft.util");

    ctClass.addField(CtField.make("private static Logger _jrLog = LoggerFactory.getLogger(\"Minecraft\");", ctClass));

    ctClass.addInterface(cp.get(JrSimpleReloadableResourceManager.class.getName()));

    ctClass.addMethod(CtNewMethod.make("" +
        "public void _jrNotifyReloadListeners() {" +
        "  notifyReloadListeners();" +
        "}", ctClass));

    CtMethod notifyReloadListeners = ctClass.getDeclaredMethod("notifyReloadListeners");
    notifyReloadListeners.instrument(new ExprEditor(){
      @Override
      public void edit(MethodCall m) throws CannotCompileException {
        if ("onResourceManagerReload".equals(m.getMethodName())) {
          m.replace("{" +
              "if (" + ReloadUtil.class.getName() + ".runReloadHandler(iresourcemanagerreloadlistener.getClass())) {" +
              "  long startTime = System.currentTimeMillis();" +
              "  $proceed($$);" +
              "  _jrLog.infoEcho(\"Reload handler ran {} ms for {}\", new Object[]{new Long(System.currentTimeMillis() - startTime), iresourcemanagerreloadlistener});" +
              "} else {" +
              "  _jrLog.infoEcho(\"Skipping reload handler {}\", new Object[]{iresourcemanagerreloadlistener});" +
              "  }" +
              "}");
        }
      }
    });
  }
}