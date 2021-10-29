package com.shattered.utilities.ecs;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author JTlr Frost - 11/14/2018 - 10:56 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ProcessComponent {

    //The conversion will be in seconds (i.e 0.1 = 100ms)
    //If left 0.f, it will trigger every frame
    float interval() default 0.f;
}

