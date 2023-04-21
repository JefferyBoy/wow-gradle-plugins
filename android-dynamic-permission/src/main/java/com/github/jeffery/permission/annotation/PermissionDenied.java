package com.github.jeffery.permission.annotation;

import com.github.jeffery.permission.PermissionHelper;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author mxlei
 * @date 2022/9/19
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionDenied{
    int requestCode() default PermissionHelper.DEFAULT_REQUEST_CODE;
}
