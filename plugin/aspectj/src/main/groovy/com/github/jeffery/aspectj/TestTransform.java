package com.github.jeffery.aspectj;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;

import org.aspectj.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author jeffery
 * @date 4/18/23
 */
public class TestTransform extends Transform {
    @Override
    public String getName() {
        return "test";
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        Set<QualifiedContent.ContentType> set = new HashSet<>();
        set.add(QualifiedContent.DefaultContentType.CLASSES);
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        Set<QualifiedContent.Scope> set = new HashSet<>();
        set.add(QualifiedContent.Scope.PROJECT);
        return set;
    }

    @Override
    public boolean isIncremental() {
        return true;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        //如果不带super，就不会生成dex文件
        super.transform(transformInvocation);
        System.out.println("context  project name = " + transformInvocation.getContext().getProjectName()
            + "context  project name = " + transformInvocation.getContext().getPath()
            + " , isIncremental = " + transformInvocation.isIncremental());
        //现在进行处理.class文件：消费型输入，需要输出给下一个任务
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        //仅仅用来查看input文件：引用型输入，无需输出，此时outputProvider为null
        //Collection<TransformInput> inputs = transformInvocation.getReferencedInputs();
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        for (TransformInput input : inputs) {
            //返回的是ImmutableJarInput。
            for (JarInput jar : input.getJarInputs()) {
                System.out.println("jar file = " + jar.getFile());
                //TODO 在这里增加处理.jar文件的代码

                //获取Transforms的输出目录
                File dest = outputProvider.getContentLocation(jar.getFile().getAbsolutePath(), jar.getContentTypes(), jar.getScopes(), Format.JAR);
                //将修改之后的文件拷贝到对应outputProvider的目录中
                FileUtil.copyFile(jar.getFile(), dest);
            }
            //返回的是ImmutableDirectoryInput
            for (DirectoryInput directory : input.getDirectoryInputs()) {
                System.out.println("directory file = " + directory.getFile());
                //TODO 在这里增加处理.class文件的代码

                //获取Transforms的输出目录
                File dest = outputProvider.getContentLocation(directory.getName(), directory.getContentTypes(), directory.getScopes(), Format.DIRECTORY);
                //将修改之后的文件拷贝到对应outputProvider的目录中
                FileUtil.copyDir(directory.getFile(), dest);
            }
        }
    }
}
