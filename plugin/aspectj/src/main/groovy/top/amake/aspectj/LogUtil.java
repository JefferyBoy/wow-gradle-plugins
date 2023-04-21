package top.amake.aspectj;


import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

/**
 * @author jeffery
 * @date 4/21/23
 */
public class LogUtil {
    private static final Logger log = Logging.getLogger("aspectj");

    public static void d(String format, Object... args) {
        log.debug(format, args);
    }

    public static void i(String format, Object... args) {
        log.info(format, args);
    }

    public static void e(String format, Object... args) {
        log.error(format, args);
    }

    public static void e(String format, Throwable throwable) {
        log.error(format, throwable);
    }

    public static void w(String format, Object... args) {
        log.warn(format, args);
    }

    public static void q(String format, Object... args) {
        log.quiet(format, args);
    }
}

