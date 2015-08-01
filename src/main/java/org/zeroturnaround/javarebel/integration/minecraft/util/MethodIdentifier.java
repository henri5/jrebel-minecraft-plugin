package org.zeroturnaround.javarebel.integration.minecraft.util;

import org.zeroturnaround.bundled.javassist.CtClass;
import org.zeroturnaround.bundled.javassist.CtMethod;
import org.zeroturnaround.bundled.javassist.NotFoundException;

public class MethodIdentifier {
  private final String methodName;
  private final CtClass[] methodArguments;

  public MethodIdentifier(String methodName, CtClass[] methodArguments) {
    this.methodName = methodName;
    this.methodArguments = methodArguments;
  }

  public MethodIdentifier(String methodName) {
    this(methodName, null);
  }

  public CtMethod getDeclaredMethod(CtClass ctClass) throws NotFoundException {
    if (methodArguments == null) {
      return ctClass.getDeclaredMethod(methodName);
    }
    return ctClass.getDeclaredMethod(methodName, methodArguments);
  }

  public String getMethodName() {
    return methodName;
  }
}
