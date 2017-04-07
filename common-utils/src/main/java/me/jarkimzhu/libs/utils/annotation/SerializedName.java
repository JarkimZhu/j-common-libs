/*
 * Copyright (c) 2014-2016. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.libs.utils.annotation;

import java.lang.annotation.*;

/**
 * @author JarkimZhu
 */

@Documented
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SerializedName {
    String value() default "getSerializedName";
}
