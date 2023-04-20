package top.amake.legu;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.AppPlugin;
import com.android.builder.model.SigningConfig;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

import java.io.File;

/**
 * @author mxlei
 * @date 2022/9/21
 */
public class LeguPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        final Logger log = Logging.getLogger(getClass());
        if (!project.getPlugins().hasPlugin(AppPlugin.class)) {
            log.error("The plugin can only used to a android application module");
            return;
        }
        LeguExtension apkProtectExtension = project.getExtensions().create("legu", LeguExtension.class);
        project.afterEvaluate(new Action<Project>() {
            @Override
            public void execute(Project project) {
                AppExtension appExtension = project.getExtensions().getByType(AppExtension.class);
                appExtension.getApplicationVariants().all(variant -> {
                    if (variant.getBuildType().isDebuggable()) {
                        return;
                    }
                    SigningConfig signingConfig = variant.getSigningConfig();
                    if (signingConfig == null) {
                        log.error("sign config is null");
                        return;
                    }
                    variant.getOutputs().all(baseVariantOutput -> {
                        File outputFile = baseVariantOutput.getOutputFile();
                        Task assembleTask = variant.getAssemble();
                        String taskName = assembleTask.getName().substring(0, 1).toUpperCase()
                            + assembleTask.getName().substring(1)
                            + "Legu";
                        project.getTasks()
                            .create(
                                taskName,
                                LeguProtectTask.class,
                                outputFile.getAbsolutePath(),
                                apkProtectExtension,
                                signingConfig
                            )
                            .dependsOn(assembleTask)
                            .setGroup("buildLegu");
                    });
                });
            }
        });
    }
}
