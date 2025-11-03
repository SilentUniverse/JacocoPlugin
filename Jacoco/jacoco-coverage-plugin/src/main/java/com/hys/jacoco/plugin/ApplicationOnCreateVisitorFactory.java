package com.hys.jacoco.plugin;

import com.android.build.api.instrumentation.AsmClassVisitorFactory;
import com.android.build.api.instrumentation.ClassContext;
import com.android.build.api.instrumentation.ClassData;
import com.android.build.api.instrumentation.InstrumentationParameters;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public abstract class ApplicationOnCreateVisitorFactory
        implements AsmClassVisitorFactory<InstrumentationParameters.None> {

    @Override
    public ClassVisitor createClassVisitor(ClassContext classContext, ClassVisitor nextClassVisitor) {
        return new ApplicationOnCreateClassVisitor(Opcodes.ASM9, nextClassVisitor);
    }

    @Override
    public boolean isInstrumentable(ClassData classData) {
        // Let the class visitor decide whether instrumentation is required. Returning true keeps
        // the visitor simple and avoids depending on class hierarchy information here.
        return true;
    }

}

