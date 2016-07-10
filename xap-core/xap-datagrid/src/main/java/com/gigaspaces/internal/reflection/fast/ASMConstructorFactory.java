/*
 * Copyright (c) 2008-2016, GigaSpaces Technologies, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gigaspaces.internal.reflection.fast;

import com.gigaspaces.internal.reflection.IConstructor;
import com.gigaspaces.internal.reflection.IParamsConstructor;

import org.objectweb.gs.asm.ClassWriter;
import org.objectweb.gs.asm.Opcodes;
import org.objectweb.gs.asm.Type;

import java.lang.reflect.Constructor;

/**
 * Creates ASM created classes that represents that provides fast default constructor "reflection"
 * mechanism.
 *
 * @author Assaf
 * @since 6.6
 */
@com.gigaspaces.api.InternalApi
public class ASMConstructorFactory {

    final private static String OBJECT_INTERNAL_NAME = Type.getInternalName(Object.class);

    final private static String CLASS_POSTFIX_NAME = "GigaspacesCtor";
    final private static String CLASS_PARAMS_POSTFIX_NAME = "GigaspacesParamsCtor";

    final private static String[] ICONSTRUCTOR_INTERNAL_NAME = new String[]{Type.getInternalName(IConstructor.class)};
    final private static String[] IPARAMSCONSTRUCTOR_INTERNAL_NAME = new String[]{Type.getInternalName(IParamsConstructor.class)};

    public static synchronized IConstructor getConstructor(Constructor originalCtor) throws NoSuchMethodException {
        return (IConstructor) getConstructorImpl(originalCtor, false);
    }

    public static synchronized IParamsConstructor getParamsConstructor(Constructor originalCtor) throws NoSuchMethodException {
        return (IParamsConstructor) getConstructorImpl(originalCtor, true);
    }

    private static synchronized Object getConstructorImpl(Constructor originalCtor, boolean params) throws NoSuchMethodException {
        final Class declaringClass = originalCtor.getDeclaringClass();
        String ownerClassName = declaringClass.getName();
        Constructor[] declaredCtors = declaringClass.getDeclaredConstructors();
        int ctorIndex = 0;
        for (; ctorIndex < declaredCtors.length; ++ctorIndex) {
            if (declaredCtors[ctorIndex].equals(originalCtor))
                break;
        }

        String classPostfixName = params ? CLASS_PARAMS_POSTFIX_NAME : CLASS_POSTFIX_NAME;
        String className = ASMFactoryUtils.getCreateClassNamePrefix(ownerClassName) + classPostfixName + ctorIndex;

        try {
            final ClassLoader targetClassLoader = ASMFactoryUtils.getClassTargetLoader(declaringClass);

            String classInternalName = className.replace('.', '/'); // build internal name for ASM

            String[] interfaces = params ? IPARAMSCONSTRUCTOR_INTERNAL_NAME : ICONSTRUCTOR_INTERNAL_NAME;

            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
            cw.visit(Opcodes.V1_5, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
                    classInternalName, null, OBJECT_INTERNAL_NAME,
                    interfaces);

            createConstructor(cw);
            if (params)
                createNewInstanceVarArgsMethod(cw, declaringClass, originalCtor);
            else
                createNewInstanceMethod(cw, declaringClass);

            cw.visitEnd();

            byte[] b = cw.toByteArray();
            Class definedClass = ASMFactoryUtils.defineClass(targetClassLoader, className, b);

            return definedClass.newInstance();
        } catch (Exception e) {
            NoSuchMethodException ex = new NoSuchMethodException("Failed generating ASM constructor wrapper: " + e.toString());
            ex.initCause(e);
            throw ex;
        }
    }

    private static void createConstructor(ClassWriter cw) {
        MethodGenerator mv = MethodGenerator.newConstructor(cw, "()V");
        mv.start();
        mv.loadThis();
        mv.invokeConstructor("java/lang/Object", "()V");
        mv.returnVoid();
    }

    private static void createNewInstanceMethod(ClassWriter cw, Class clz) {
        MethodGenerator mv = MethodGenerator.newMethod(cw, "newInstance", "()Ljava/lang/Object;");
        mv.start();
        mv.newInstance(Type.getInternalName(clz));
        mv.dup();
        mv.invokeConstructor(Type.getInternalName(clz), "()V");
        mv.returnObject();
    }

    private static void createNewInstanceVarArgsMethod(ClassWriter cw, Class clz, Constructor constructor) {
        MethodGenerator mv = MethodGenerator.newMethod(cw, "newInstance", "([Ljava/lang/Object;)Ljava/lang/Object;");
        mv.start();
        mv.newInstance(Type.getInternalName(clz));
        mv.dup();
        Class[] parameterTypes = constructor.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            mv.loadArrayItemFromVariable(1, i);
            mv.unboxIfNeeded(parameterTypes[i]);
        }
        mv.invokeConstructor(Type.getInternalName(clz), Type.getConstructorDescriptor(constructor));
        mv.returnObject();
    }
}
