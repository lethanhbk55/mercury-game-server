package com.mercury.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.mercury.server.message.MGSMessageType;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandProcessor {

	MGSMessageType type();
}
