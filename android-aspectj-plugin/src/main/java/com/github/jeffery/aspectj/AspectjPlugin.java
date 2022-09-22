package com.github.jeffery.aspectj;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.AppPlugin;
import com.android.build.gradle.LibraryPlugin;
import com.android.build.gradle.api.ApplicationVariant;
import com.android.build.gradle.api.BaseVariantOutput;

import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.MessageHandler;
import org.aspectj.tools.ajc.Main;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.UnknownTaskException;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.compile.JavaCompile;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * @author mxlei
 * @date 2022/9/20
 */
public class AspectjPlugin implements Plugin<Project> {
    private final Logger log = Logging.getLogger(getClass());

    @Override
    public void apply(Project project) {
        PluginContainer plugins = project.getPlugins();
        if (!plugins.hasPlugin(AppPlugin.class) && !plugins.hasPlugin(LibraryPlugin.class)) {
            return;
        }
        Dependency rt = project.getDependencies().create("org.aspectj:aspectjrt:1.9.9.1");
        project.getDependencies().add("implementation", rt);

        log.quiet("config aspectj for project " + project.getName());
        final AppExtension appExtension = project.getExtensions().getByType(AppExtension.class);
        appExtension.getApplicationVariants().all(new Action<ApplicationVariant>() {
            @Override
            public void execute(ApplicationVariant variant) {
                variant.getOutputs().all(new Action<BaseVariantOutput>() {
                    @Override
                    public void execute(BaseVariantOutput variantOutput) {
                        TaskProvider<JavaCompile> javaCompileProvider = variant.getJavaCompileProvider();
                        JavaCompile javaCompile = javaCompileProvider.get();
                        List<File> bootClassPathList = appExtension.getBootClasspath();
                        StringBuilder bootClassPath = new StringBuilder();
                        for (File file : bootClassPathList) {
                            bootClassPath.append(file.getPath()).append(File.pathSeparator);
                        }
                        bootClassPath.deleteCharAt(bootClassPath.length() - 1);
                        String javacOutDir = "";
                        Task kotlinCompileTask = null;
                        try {
                            // flavor-buildType组合的名称
                            // 如 free-debug
                            String flavor = variant.getFlavorName();
                            if (!flavor.isEmpty()) {
                                flavor = flavor.substring(0, 1).toUpperCase() + flavor.substring(1);
                            }
                            String buildType = variant.getBuildType().getName();
                            if (!buildType.isEmpty()) {
                                buildType = buildType.substring(0, 1).toUpperCase() + buildType.substring(1);
                            }
                            javacOutDir = variant.getFlavorName() + buildType;
                            String kotlinCompileTaskName = "compile" + flavor + buildType + "Kotlin";
                            log.debug("kotlin task: {}", kotlinCompileTaskName);
                            kotlinCompileTask = project.getTasks().getByName(kotlinCompileTaskName);
                        } catch (UnknownTaskException e) {
                            e.printStackTrace();
                        }
                        // kotlin
                        if (kotlinCompileTask != null) {
                            String finalJavacOutDir = javacOutDir;
                            kotlinCompileTask.doLast(task -> aspectjWeaver(
                                bootClassPath.toString(),
                                project.getBuildDir().getPath() + "/tmp/kotlin-classes/" + finalJavacOutDir,
                                javaCompile.getClasspath().getAsPath()
                            ));
                        }
                        // java
                        log.debug("java compile task: {}", javaCompile.getName());
                        javaCompile.doLast(task -> aspectjWeaver(
                            bootClassPath.toString(),
                            javaCompile.getDestinationDir().toString(),
                            javaCompile.getClasspath().getAsPath()
                        ));
                    }
                });
            }
        });
    }

    private void aspectjWeaver(String bootClassPath, String inputOutputPath, String aspectPath) {
        String[] javaArgs = new String[]{
            "-showWeaveInfo",
            "-1.8",
            "-aspectpath", aspectPath,
            "-classpath", aspectPath,
            "-inpath", inputOutputPath,
            "-d", inputOutputPath,
            "-bootclasspath", bootClassPath};
        log.debug("aspectj javaArgs: " + Arrays.toString(javaArgs));
        MessageHandler handler = new MessageHandler(true);
        new Main().run(javaArgs, handler);
        for (IMessage message : handler.getMessages(null, true)) {
            IMessage.Kind kind = message.getKind();
            if (IMessage.ABORT.equals(kind) || IMessage.ERROR.equals(kind) || IMessage.FAIL.equals(kind)) {
                log.error(message.getMessage(), message.getThrown());
            } else if (IMessage.WARNING.equals(kind)) {
                log.warn(message.getMessage(), message.getThrown());
            } else if (IMessage.INFO.equals(kind)) {
                log.info(message.getMessage(), message.getThrown());
            } else if (IMessage.DEBUG.equals(kind)) {
                log.debug(message.getMessage(), message.getThrown());
            }
        }
    }
}
