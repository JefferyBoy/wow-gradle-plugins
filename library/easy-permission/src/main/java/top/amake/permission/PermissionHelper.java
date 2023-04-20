package top.amake.permission;

import android.content.Context;

/**
 * @author mxlei
 * @date 2022/9/19
 */
public class PermissionHelper {
    public static final int DEFAULT_REQUEST_CODE = 1;
    private Context context;
    private static PermissionHelper instance;

    private PermissionHelper() {
    }

    public static PermissionHelper getInstance() {
        if (instance == null) {
            instance = new PermissionHelper();
        }
        return instance;
    }

    public void init(Context context) {
        if (this.context == null && context != null) {
            this.context = context.getApplicationContext();
        }
    }

    public Context getContext() {
        return context;
    }
}
