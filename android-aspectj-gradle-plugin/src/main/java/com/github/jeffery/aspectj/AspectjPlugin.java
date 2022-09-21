package com.github.jeffery.aspectj;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.AppPlugin;
import com.android.build.gradle.LibraryPlugin;
import com.android.build.gradle.api.ApplicationVariant;

import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.MessageHandler;
import org.aspectj.tools.ajc.Main;
import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.plugins.PluginContainer;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.compile.JavaCompile;
import org.slf4j.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * @author mxlei
 * @date 2022/9/20
 */
public class AspectjPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        PluginContainer plugins = project.getPlugins();
        final Logger log = project.getLogger();
        if (!plugins.hasPlugin(AppPlugin.class) && !plugins.hasPlugin(LibraryPlugin.class)) {
            return;
        }
        log.debug("config aspectj for project " + project.getName());
        final AppExtension appExtension = project.getExtensions().getByType(AppExtension.class);
        appExtension.getApplicationVariants().all(new Action<ApplicationVariant>() {
            @Override
            public void execute(ApplicationVariant variant) {
                TaskProvider<JavaCompile> javaCompileProvider = variant.getJavaCompileProvider();
                javaCompileProvider.configure(new Action<JavaCompile>() {
                    @Override
                    public void execute(final JavaCompile javaCompile) {
                        javaCompile.doLast(new Action<Task>() {
                            @Override
                            public void execute(Task task) {
                                List<File> bootClassPathList = appExtension.getBootClasspath();
                                StringBuilder bootClassPath = new StringBuilder();
                                for (File file : bootClassPathList) {
                                    bootClassPath.append(file.getPath()).append(":");
                                }
                                bootClassPath.deleteCharAt(bootClassPath.length() - 1);
                                String[] args = new String[]{"-showWeaveInfo",
                                    "-1.8",
                                    "-inpath", javaCompile.getDestinationDir().toString(),
                                    "-aspectpath", javaCompile.getClasspath().getAsPath(),
                                    "-d", javaCompile.getDestinationDir().toString(),
                                    "-classpath", javaCompile.getDestinationDir().getAbsolutePath(),
                                    "-bootclasspath", bootClassPath.toString()};
                                log.debug("ajc args: " + Arrays.toString(args));
                                MessageHandler handler = new MessageHandler(true);
                                new Main().run(args, handler);
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
                        });
                    }
                });
            }
        });
    }
}
