package top.amake.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mxlei
 * @date 2022/9/20
 */
public class PermissionActivity extends Activity {

    public static final String EXTRA_PERMISSIONS = "permissions";
    public static final String EXTRA_REQUEST_CODE = "requestCode";
    private static final Map<Integer, PermissionCallback> callbackList = new HashMap<>();

    public static void requestPermissions(Context context, Intent intent, PermissionCallback callback) {
        if (callback != null) {
            callbackList.put(intent.getIntExtra(EXTRA_REQUEST_CODE, 1), callback);
        }
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent startIntent = getIntent();
        String[] pms = startIntent.getStringArrayExtra(EXTRA_PERMISSIONS);
        int reqCode = startIntent.getIntExtra(EXTRA_REQUEST_CODE, 1);
        boolean needReqPermission = false;
        if (pms != null && pms.length > 0 && Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            for (String pm : pms) {
                if (checkSelfPermission(pm) != PackageManager.PERMISSION_GRANTED) {
                    needReqPermission = true;
                    break;
                }
            }
        }
        if (needReqPermission && Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            requestPermissions(pms, reqCode);
        } else {
            List<PermissionResult> results = new ArrayList<>();
            if (pms != null && pms.length > 0) {
                for (String pm : pms) {
                    PermissionResult result = new PermissionResult();
                    result.setRequestCode(reqCode);
                    result.setPermission(pm);
                    result.setGranted(true);
                    result.setForbidden(false);
                    results.add(result);
                }
            }
            dispatchPermissionResultCallback(reqCode, results);
        }
    }

    private void dispatchPermissionResultCallback(int requestCode, List<PermissionResult> resultList) {
        PermissionCallback callback = callbackList.get(requestCode);
        if (callback != null) {
            callbackList.remove(requestCode);
            callback.onPermissionResult(requestCode, resultList);
        }
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        final List<PermissionResult> permissionResultItemList = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            PermissionResult item = new PermissionResult();
            item.setRequestCode(requestCode);
            item.setPermission(permissions[i]);
            item.setGranted(grantResults[i] == PackageManager.PERMISSION_GRANTED);
            if (item.isGranted()) {
                item.setForbidden(false);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //shouldShowRequestPermissionRationale返回结果
                    //1.都没有请求过这个权限，用户不一定会拒绝你，所以你不用解释，故返回false;
                    //2.请求了但是被拒绝了，此时返回true，意思是你该向用户好好解释下了；
                    //3.请求权限被禁止了，也不给你弹窗提醒了，所以你也不用解释了，故返回fasle;
                    //4.请求被允许了，都给你权限了，还解释个啥，故返回false。
                    // 请求被拒绝且不需要解释就是被系统禁止了权限，某些rom在连续几次申请权限拒绝后将禁止申请权限，不再谈框
                    item.setForbidden(!shouldShowRequestPermissionRationale(permissions[i]));
                } else {
                    item.setForbidden(false);
                }
            }
            permissionResultItemList.add(item);
            dispatchPermissionResultCallback(requestCode, permissionResultItemList);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
