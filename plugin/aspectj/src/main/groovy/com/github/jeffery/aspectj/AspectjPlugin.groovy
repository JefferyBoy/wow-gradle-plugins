package com.github.jeffery.aspectj

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.plugins.PluginContainer

/**
 * @author mxlei
 * @date 2022/9/20
 */
class AspectjPlugin implements Plugin<Project> {
    private final Logger log = Logging.getLogger(getClass())
    private AspectjTransform aspectjTransform
    private Project project

    @Override
    void apply(Project project) {
        PluginContainer plugins = project.getPlugins()
        if (!plugins.hasPlugin(AppPlugin.class) && !plugins.hasPlugin(LibraryPlugin.class)) {
            return
        }
        this.project = project
        aspectjTransform = new AspectjTransform(project)
        project.getDependencies().add("implementation", "org.aspectj:aspectjrt:1.9.19")
        final AppExtension appExtension = project.getExtensions().findByType(AppExtension.class);
        final LibraryExtension libExtension = project.getExtensions().findByType(LibraryExtension.class);
        if (libExtension != null) {
            libExtension.registerTransform(aspectjTransform)
//            libExtension.registerTransform(new TestTransform())
        }
        if (appExtension != null) {
            appExtension.registerTransform(aspectjTransform)
//            appExtension.registerTransform(new TestTransform())
        }
    }
}
