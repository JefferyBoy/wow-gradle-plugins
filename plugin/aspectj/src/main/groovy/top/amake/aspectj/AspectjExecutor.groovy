package top.amake.aspectj

import org.aspectj.bridge.IMessage
import org.aspectj.bridge.MessageHandler
import org.aspectj.tools.ajc.Main
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging

import java.util.regex.Pattern

class AspectjExecutor {

    private static final Logger log = Logging.getLogger(getClass())

    static Collection<File> aspectjWeaver(String bootClassPath, String inputPath, String outputPath, String aspectPath, String classPath) {
        String[] javaArgs = new String[]{
//            "-verbose",
            "-showWeaveInfo",
            "-1.8",
            "-aspectpath", aspectPath,
            "-inpath", inputPath,
            "-d", outputPath,
            "-bootclasspath", bootClassPath,
            "-classpath", classPath
        }
//        log.quiet("aspectj javaArgs: " + Arrays.toString(javaArgs))
        MessageHandler handler = new MessageHandler(true)
        new Main().run(javaArgs, handler)
        for (IMessage message : handler.getMessages(null, true)) {
            IMessage.Kind kind = message.getKind();
            if (IMessage.ABORT == kind || IMessage.ERROR == kind || IMessage.FAIL == kind) {
                log.error(message.getMessage(), message.getThrown());
            } else if (IMessage.WARNING == kind) {
                log.warn(message.getMessage(), message.getThrown());
            } else if (IMessage.INFO == kind || IMessage.WEAVEINFO == kind) {
                log.info(message.getMessage(), message.getThrown());
            } else if (IMessage.DEBUG == kind || IMessage.USAGE == kind) {
                log.debug(message.getMessage(), message.getThrown());
            }
        }

        return listGeneratedAspectFile(new File(outputPath))
    }

    private static Collection<File> listGeneratedAspectFile(File dir) {
        def generatedFiles = new HashSet()
        def ajcPattern = Pattern.compile("^.+\\u0024AjcClosure\\d+\\.class\$")
        if (!dir.exists()) {
            return generatedFiles
        }
        if (dir.isDirectory()) {
            def files = dir.listFiles()
            if (files != null && files.length > 0) {
                for (final def t in files) {
                    generatedFiles.addAll(listGeneratedAspectFile(t))
                }
            }
        } else {
            if (ajcPattern.matcher(dir.name).matches()) {
                generatedFiles.add(dir)
            }
        }
        return generatedFiles
    }
}
