package com.lioncorp.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface LionImpl {
	Class<?> ApiIfaceClazz();

	Class<?> ApiProcessorClazz();

	String ApiName();

	String description() default "";
}

