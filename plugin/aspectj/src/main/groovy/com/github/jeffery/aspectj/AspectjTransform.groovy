package com.github.jeffery.aspectj

import aj.org.objectweb.asm.AnnotationVisitor
import aj.org.objectweb.asm.ClassReader
import aj.org.objectweb.asm.ClassVisitor
import aj.org.objectweb.asm.Opcodes
import com.android.build.api.transform.*
import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.pipeline.TransformManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.aspectj.util.FileUtil
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

import java.util.regex.Pattern
import java.util.stream.Collectors
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

class AspectjTransform extends Transform {
    private Project project
    private String baseDir
    private Gson gson
    private AspectjCache aspectCache
    private File aspectCacheFile
    private String aspectCacheJson
    private final Logger log = Logging.getLogger(getClass())
    private static final String ASPECT_TRANSFORM_NAME = "aspectjTransform"

    AspectjTransform(Project project) {
        this.project = project
    }

    private void initTransform(TransformInvocation transform) {
        baseDir = "${project.buildDir}/intermediates/transforms/$ASPECT_TRANSFORM_NAME"
        def baseDirFile = new File(baseDir)
        if (!baseDirFile.exists()) {
            baseDirFile.mkdirs()
        }
        gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .create()
        aspectCacheFile = new File("${baseDir}/aspect${transform.context.variantName.capitalize()}.json")
        aspectCache = new AspectjCache()
        if (aspectCacheFile.exists()) {
            aspectCacheJson = new String(aspectCacheFile.readBytes())
            def cache = gson.fromJson(aspectCacheJson, AspectjCache)
            aspectCache.aspectClasses.addAll(cache.aspectClasses)
            aspectCache.aspectJoinPoints.putAll(cache.aspectJoinPoints)
        }
    }

    @Override
    String getName() {
        return ASPECT_TRANSFORM_NAME
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        def set = new HashSet<>()
        set.add(QualifiedContent.Scope.PROJECT)
        return set
    }

    @Override
    boolean isIncremental() {
        return true
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
//        log.quiet("transform incremental: ${transformInvocation.incremental}")
        initTransform(transformInvocation)
        def variantName = transformInvocation.context.variantName
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        if (transformInvocation.isIncremental()) {
            transformInvocation.inputs.each { input ->
                input.jarInputs.each { jar ->
                    File jarDest = outputProvider.getContentLocation(jar.getFile().getAbsolutePath(), jar.getContentTypes(), jar.getScopes(), Format.JAR);
                    switch (jar.status) {
                        case Status.ADDED:
                            FileUtil.copyFile(jar.file, jarDest)
                            aspectCache.addAspect(findAspectOfFile(jarDest))
                            break
                        case Status.CHANGED:
                            FileUtil.copyFile(jar.file, jarDest)
                            aspectCache.removeAspect(jarDest)
                            aspectCache.addAspect(findAspectOfFile(jarDest))
                            break
                        case Status.REMOVED:
                            aspectCache.removeAspect(jarDest)
                            jarDest.delete()
                            break
                    }
                }
                input.directoryInputs.each { dirInput ->
                    File dirDest = outputProvider.getContentLocation(dirInput.getName(), dirInput.getContentTypes(), dirInput.getScopes(), Format.DIRECTORY);
                    def fileTempDir = new File(baseDir, "temp")
                    def needWeaver = false
                    dirInput.changedFiles.each { file, status ->
                        def fileDest = new File(dirDest, file.path.replaceFirst(dirInput.file.path, ""))
                        def fileTemp = new File(fileTempDir, file.path.replaceFirst(dirInput.file.path, ""))
                        switch (status) {
                            case Status.ADDED:
//                                log.quiet("add file ${file.path}")
                                FileUtil.copyFile(file, fileDest)
                                FileUtil.copyFile(file, fileTemp)
                                needWeaver = true
                                aspectCache.addAspect(findAspectOfFile(fileDest))
                                break
                            case Status.CHANGED:
//                                log.quiet("changed file ${file.path}")
                                aspectCache.removeAspect(fileDest)
                                aspectCache.removeJoinPoint(fileDest)
                                FileUtil.copyFile(file, fileDest)
                                FileUtil.copyFile(file, fileTemp)
                                needWeaver = true
                                aspectCache.addAspect(findAspectOfFile(fileDest))
                                break
                            case Status.REMOVED:
//                                log.quiet("remove file ${file.path}")
                                aspectCache.removeAspect(fileDest)
                                aspectCache.removeJoinPoint(fileDest)
                                break
                        }
                    }
                    if (needWeaver) {
                        processAspectj(variantName, fileTempDir.path, dirDest.path)
                        FileUtil.deleteContents(fileTempDir)
                    }
                }
            }
        } else {
            aspectCache.clear()
            outputProvider.deleteAll()
            Collection<File> changedFiles = new ArrayList<>()
            transformInvocation.inputs.each { input ->
                input.jarInputs.each { jar ->
//                    log.quiet("jarInput ${jar.file.path}")
                    File jarDest = outputProvider.getContentLocation(jar.getFile().getAbsolutePath(), jar.getContentTypes(), jar.getScopes(), Format.JAR);
                    FileUtil.copyFile(jar.file, jarDest)
                    aspectCache.addAspect(findAspectOfFile(jarDest))
                }
                input.directoryInputs.each { dir ->
//                    log.quiet("directoryInput ${dir.file.path}")
                    File dirDest = outputProvider.getContentLocation(dir.getName(), dir.getContentTypes(), dir.getScopes(), Format.DIRECTORY);
                    FileUtil.copyDir(dir.getFile(), dirDest)
                    aspectCache.addAspect(findAspectOfFile(dirDest))
                    changedFiles.add(dirDest)
                }
            }
            changedFiles.forEach {
                processAspectj(variantName, it.path, it.path)
            }
        }
        def json = gson.toJson(aspectCache)
        if (aspectCacheJson != json) {
            aspectCacheJson = json
            def fo = new FileOutputStream(aspectCacheFile)
            fo.write(json.getBytes())
            fo.close()
        }
    }

    /**
     * 处理Aspect织入
     * @param variantName 变体名称
     * */
    private void processAspectj(String variantName, String inputPath, String outputPath) {
        def aspectFileSet = aspectCache.aspectClasses
//        log.quiet("processAspectj ${variantName}")
//        log.quiet("inputPath =  ${inputPath}")
//        log.quiet("aspectPath =  ${aspectFileSet.join(":")}")
        if (aspectFileSet.isEmpty()) {
            return
        }
        def start = System.currentTimeMillis()
        def app = project.extensions.findByType(AppExtension)
        def lib = project.extensions.findByType(LibraryExtension)
        def bootClassPath = ""
        def classPath = ""
        def aspectPath = aspectFileSet
            .stream()
            .map({ it.substring(0, it.lastIndexOf('/')) })
            .collect(Collectors.toSet())
            .join(":")
        if (lib != null) {
            lib.libraryVariants.forEach { variant ->
                if (variant.name == variantName) {
                    classPath = variant.javaCompileProvider
                                       .get()
                                       .getClasspath()
                                       .collect { it.path }
                                       .join(":")
                    bootClassPath = lib.getBootClasspath()
                                       .collect { it.path }
                                       .join(":")
                }
            }
        }
        if (app != null) {
            app.applicationVariants.all { ApplicationVariant variant ->
                if (variant.name == variantName) {
                    classPath = variant.javaCompileProvider
                                       .get()
                                       .getClasspath()
                                       .collect { it.path }
                                       .join(":")
                    bootClassPath = app.getBootClasspath()
                                       .collect { it.path }
                                       .join(":")
                }
            }
        }
        def ajcFiles = AspectjExecutor.aspectjWeaver(
            bootClassPath,
            inputPath,
            outputPath,
            aspectPath,
            classPath
        )
        def closurePattern = Pattern.compile("\\u0024AjcClosure\\d+\\.class\$")
        ajcFiles.forEach {
            String key = "${it.parent}/${it.name.replaceAll(closurePattern.pattern(), ".class")}"
            def value = aspectCache.aspectJoinPoints.get(key)
            if (value == null) {
                value = new HashSet<String>()
                aspectCache.aspectJoinPoints.put(key, value)
            }
            value.add(it.path)
        }
        log.quiet("aspect transform complete in ${System.currentTimeMillis() - start} ms")
    }

    /**
     * 处理类文件或路径，检查类是否是切面类
     * @param file 文件
     * */
    private Collection<File> findAspectOfFile(File file) {
        Collection<File> result = new HashSet<File>()
        if (!file.exists()) {
            return result
        }
        if (file.isDirectory()) {
            var files = file.listFiles()
            if (files != null && files.length > 0) {
                for (File f : files) {
                    result.addAll(findAspectOfFile(f))
                }
            }
        } else if (file.name.endsWith(".class")) {
//            log.quiet("process file ${file.path}")
            def classVisitor = aspectClassVisitor(new FileInputStream(file))
            if (classVisitor != null && classVisitor.isAspect) {
                result.add(file)
            }
        } else if (file.name.endsWith(".jar")) {
//        log.quiet("process file ${jar.file.path}")
            ZipFile zipFile = new ZipFile(file.absolutePath)
            var entries = zipFile.entries()
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement()
                if (!entry.isDirectory()) {
                    def classVisitor = aspectClassVisitor(zipFile.getInputStream(entry))
                    if (classVisitor != null && classVisitor.isAspect) {
                        result.add(file)
                        break
                    }
                }
            }
            zipFile.close()
        }
        return result
    }

    /**
     * 处理class文件
     * @param stream 文件输入流
     * @return true表示是切面，false表示非切面
     * */
    private AspectjClassVisitor aspectClassVisitor(InputStream stream) {
        try {
            ClassReader cr = new ClassReader(stream)
            AspectjClassVisitor classVisitor = new AspectjClassVisitor(Opcodes.ASM7)
            cr.accept(classVisitor, ClassReader.SKIP_CODE)
            return classVisitor
        } catch (Exception ignored) {
        }
        stream.close()
        return null
    }

    private class AspectjClassVisitor extends ClassVisitor {

        private boolean isAspect = false
        private String signatureName

        AspectjClassVisitor(int api) {
            super(api)
        }

        AspectjClassVisitor(int api, ClassVisitor classVisitor) {
            super(api, classVisitor)
        }

        @Override
        void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces)
            signatureName = name
        }

        @Override
        AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            if (!isAspect && descriptor == "Lorg/aspectj/lang/annotation/Aspect;") {
                isAspect = true
            }
            return super.visitAnnotation(descriptor, visible)
        }
    }
}