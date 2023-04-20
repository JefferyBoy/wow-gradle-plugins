package top.amake.permission;

/**
 * @author mxlei
 * @date 2022/9/19
 */
public class PermissionResult {
    private String permission;
    private boolean granted;
    private boolean forbidden;
    private int requestCode;

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public boolean isGranted() {
        return granted;
    }

    public void setGranted(boolean granted) {
        this.granted = granted;
    }

    public boolean isForbidden() {
        return forbidden;
    }

    public void setForbidden(boolean forbidden) {
        this.forbidden = forbidden;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    @Override
    public String toString() {
        return "PermissionResult{" +
            "permission='" + permission + '\'' +
            ", granted=" + granted +
            ", forbidden=" + forbidden +
            ", requestCode=" + requestCode +
            '}';
    }
}
