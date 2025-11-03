package com.hys.jacoco.plugin;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

class ApplicationOnCreateClassVisitor extends ClassVisitor {

    private static final String APPLICATION_CLASS = "android/app/Application";
    private static final String MULTIDEX_APPLICATION_CLASS = "androidx/multidex/MultiDexApplication";

    private enum InstrumentMode {
        NONE,
        ROOT_APPLICATION,
        SUBCLASS_APPLICATION
    }

    private final int asmApi;
    private InstrumentMode instrumentMode = InstrumentMode.NONE;
    private String superName;
    private boolean hasOnCreate;

    ApplicationOnCreateClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
        this.asmApi = api;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.superName = superName;
        this.instrumentMode = resolveInstrumentMode(superName);
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

        if (instrumentMode == InstrumentMode.NONE) {
            return mv;
        }

        if ("onCreate".equals(name) && "()V".equals(descriptor)) {
            hasOnCreate = true;
            return new ApplicationOnCreateMethodVisitor(asmApi, mv, access, name, descriptor);
        }

        return mv;
    }

    @Override
    public void visitEnd() {
        if (instrumentMode == InstrumentMode.ROOT_APPLICATION && !hasOnCreate) {
            createOnCreateMethod();
        }
        super.visitEnd();
    }

    private void createOnCreateMethod() {
        MethodVisitor mv = super.visitMethod(Opcodes.ACC_PROTECTED, "onCreate", "()V", null, null);
        if (mv == null) {
            return;
        }

        mv.visitCode();
        // super.onCreate();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superName, "onCreate", "()V", false);

        // CoverageHelper.initCoverageDump(this);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                "com/hys/jacoco_runtime/CoverageHelper",
                "initCoverageDump",
                "(Landroid/content/Context;)V",
                false);

        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(2, 1);
        mv.visitEnd();
    }

    private InstrumentMode resolveInstrumentMode(String superName) {
        if (superName == null) {
            return InstrumentMode.NONE;
        }

        if (APPLICATION_CLASS.equals(superName) || MULTIDEX_APPLICATION_CLASS.equals(superName)) {
            return InstrumentMode.ROOT_APPLICATION;
        }

        if (superName.endsWith("/Application")) {
            return InstrumentMode.SUBCLASS_APPLICATION;
        }

        return InstrumentMode.NONE;
    }
}

