package top.amake.permission;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.view.View;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import top.amake.permission.annotation.Permission;
import top.amake.permission.annotation.PermissionDenied;

/**
 * @author mxlei
 * @date 2022/9/19
 */
@Aspect
public class PermissionAspect {

    public static PermissionAspect aspectOf() {
        return new PermissionAspect();
    }

    @Pointcut("execution(@top.amake.permission.annotation.Permission * *(..)) && @annotation(permission)")
    public void requestPermission(Permission permission) {
    }

    @Around("requestPermission(permission)")
    public void aroundRequestPermission(final ProceedingJoinPoint joinPoint, Permission permission) {
        if (permission.value() == null || permission.value().length == 0) {
            return;
        }
        final Object callFromObj = joinPoint.getThis();
        Context context = null;
        Intent intent = new Intent();
        if (callFromObj instanceof Activity) {
            context = (Context) callFromObj;
        } else if (callFromObj instanceof Fragment) {
            context = (Context) ((Fragment) callFromObj).getActivity();
        } else if (callFromObj instanceof androidx.fragment.app.Fragment) {
            context = ((androidx.fragment.app.Fragment) callFromObj).requireActivity();
        } else if (callFromObj instanceof View) {
            View v = (View) callFromObj;
            context = getActivity(v.getContext());
        }
        if (context == null) {
            context = PermissionHelper.getInstance().getContext();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(0, 0);
        }
        intent.setComponent(new ComponentName(context, PermissionActivity.class));
        intent.putExtra(PermissionActivity.EXTRA_REQUEST_CODE, permission.requestCode());
        intent.putExtra(PermissionActivity.EXTRA_PERMISSIONS, permission.value());
        PermissionActivity.requestPermissions(context, intent, new PermissionCallback() {

            @Override
            public void onPermissionResult(int requestCode, List<PermissionResult> result) {
                dispatchPermissionResult(joinPoint, requestCode, result);
            }
        });
    }

    private void dispatchPermissionResult(ProceedingJoinPoint joinPoint, int requestCode, List<PermissionResult> result) {
        boolean grantAll = true;
        for (PermissionResult item : result) {
            if (!item.isGranted()) {
                grantAll = false;
                break;
            }
        }
        if (grantAll) {
            try {
                joinPoint.proceed(joinPoint.getArgs());
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else {
            Object caller = joinPoint.getThis();
            List<Method> methodList = getMethodWithAnnotation(caller, PermissionDenied.class);
            for (Method method : methodList) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                PermissionDenied permissionDenied = method.getAnnotation(PermissionDenied.class);
                if (permissionDenied == null || permissionDenied.requestCode() != requestCode) {
                    continue;
                }
                switch (parameterTypes.length) {
                    case 0:
                        try {
                            method.invoke(caller);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 1:
                        if (!parameterTypes[0].isAssignableFrom(List.class)) {
                            throw new IllegalArgumentException("The method with PermissionDenied annotation parameter must be a List");
                        }
                        try {
                            method.invoke(caller, result);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException("The method with PermissionDenied annotaion parameter not valid (List<PermissionResult>)", e);
                        } catch (InvocationTargetException e) {
                            throw new RuntimeException("The method with PermissionDenied annotaion parameter not valid (List<PermissionResult>)", e);
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("The method with PermissionDenied annotation must have 0 or 1 parameter");
                }
            }
        }
    }

    private List<Method> getMethodWithAnnotation(Object obj, Class annotationCls) {
        List<Method> methodList = new ArrayList<>();
        Class cls = obj.getClass();
        Method[] methods = cls.getDeclaredMethods();
        for (Method method : methods) {
            Annotation annotation = method.getAnnotation(annotationCls);
            if (annotation != null) {
                method.setAccessible(true);
                methodList.add(method);
            }
        }
        return methodList;
    }

    private Activity getActivity(Context context) {
        if (context == null) {
            return null;
        }
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ContextWrapper) {
            return getActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }
}
