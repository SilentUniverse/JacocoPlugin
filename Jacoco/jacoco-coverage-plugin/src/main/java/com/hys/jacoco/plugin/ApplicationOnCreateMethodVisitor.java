package com.hys.jacoco.plugin;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

class ApplicationOnCreateMethodVisitor extends AdviceAdapter {

    private static final String COVERAGE_HELPER = "com/hys/jacoco_runtime/CoverageHelper";
    private static final String INIT_METHOD_NAME = "initCoverageDump";
    private static final String INIT_METHOD_DESC = "(Landroid/content/Context;)V";

    protected ApplicationOnCreateMethodVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
        super(api, methodVisitor, access, name, descriptor);
    }

    @Override
    protected void onMethodEnter() {
        // this
        visitVarInsn(Opcodes.ALOAD, 0);
        visitMethodInsn(Opcodes.INVOKESTATIC, COVERAGE_HELPER, INIT_METHOD_NAME, INIT_METHOD_DESC, false);
    }
}

