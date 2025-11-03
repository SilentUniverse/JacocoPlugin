package com.hys.jacoco.plugin;

import com.android.build.api.dsl.ApplicationBuildType;
import com.android.build.api.dsl.ApplicationExtension;
import com.android.build.api.instrumentation.FramesComputationMode;
import com.android.build.api.instrumentation.InstrumentationScope;
import com.android.build.api.variant.AndroidComponentsExtension;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.plugins.PluginManager;
import kotlin.Unit;

/**
 * Gradle plugin entry point that wires Jacoco runtime initialization into Android applications
 * via bytecode instrumentation.
 */
public class JacocoAutoInitPlugin implements Plugin<Project> {

    private static final String ANDROID_APPLICATION_PLUGIN = "com.android.application";
    private static final String RUNTIME_COORDINATE = "com.hys.jacoco:jacoco-coverage-runtime:1.0.0";

    @Override
    public void apply(Project project) {
        PluginManager pluginManager = project.getPluginManager();
        pluginManager.withPlugin(ANDROID_APPLICATION_PLUGIN, appliedPlugin -> configureForApplication(project));
        pluginManager.withPlugin("com.android.library",
                plugin -> project.getLogger().warn("Jacoco auto-init plugin is intended for application modules only."));
    }

    private void configureForApplication(Project project) {
        Logger logger = project.getLogger();

        AndroidComponentsExtension<?, ?, ?> androidComponents =
                project.getExtensions().getByType(AndroidComponentsExtension.class);

        androidComponents.onVariants(androidComponents.selector().all(), variant -> {
            variant.getInstrumentation().transformClassesWith(
                    ApplicationOnCreateVisitorFactory.class,
                    InstrumentationScope.PROJECT,
                    parameters -> Unit.INSTANCE
            );
            variant.getInstrumentation().setAsmFramesComputationMode(FramesComputationMode.COPY_FRAMES);
        });

        ApplicationExtension applicationExtension = project.getExtensions().getByType(ApplicationExtension.class);
        applicationExtension.getBuildTypes().configureEach(buildType -> enableCoverageDefaults(buildType, logger));

        addRuntimeDependency(project, logger);
    }

    private void enableCoverageDefaults(ApplicationBuildType buildType, Logger logger) {
        boolean debugLike = buildType.getName().toLowerCase(Locale.US).contains("debug");

        buildType.setEnableAndroidTestCoverage(debugLike);
        buildType.setEnableUnitTestCoverage(debugLike);

        if (debugLike) {
            logger.debug("Jacoco auto-init enabled coverage defaults for build type {}", buildType.getName());
        }
    }

    private void addRuntimeDependency(Project project, Logger logger) {
        Project runtimeProject = project.findProject(":jacoco-coverage-runtime");
        if (runtimeProject != null) {
            Map<String, Object> dependencyNotation = Collections.singletonMap("path", ":jacoco-coverage-runtime");
            project.getDependencies().add("implementation", project.getDependencies().project(dependencyNotation));
            logger.info("Jacoco auto-init plugin linked local project ':jacoco-coverage-runtime'.");
            return;
        }

        project.getDependencies().add("implementation", RUNTIME_COORDINATE);
        logger.info("Jacoco auto-init plugin added runtime dependency {}", RUNTIME_COORDINATE);
    }
}

