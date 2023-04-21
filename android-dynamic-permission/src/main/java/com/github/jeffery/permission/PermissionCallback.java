package com.github.jeffery.permission;

import java.util.List;

/**
 * @author mxlei
 * @date 2022/9/20
 */
public interface PermissionCallback {
    /**
     * 申请权限结果
     */
    void onPermissionResult(int requestCode, List<PermissionResult> result);
}
