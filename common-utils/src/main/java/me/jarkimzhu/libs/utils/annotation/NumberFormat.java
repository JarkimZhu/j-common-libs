/*
 * Copyright (c) 2014-2016. JarkimZhu
 * This software can not be used privately without permission
 */

package me.jarkimzhu.libs.utils.annotation;

import java.lang.annotation.*;

/**
 * Created on 2016/8/22.
 *
 * @author JarkimZhu
 * @since JDK1.8
 */
@Documented
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NumberFormat {
    String value() default "0.00";

    boolean stringify() default false;
}
