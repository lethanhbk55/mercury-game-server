package com.mercury.server.annotation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

import com.mercury.server.message.MGSMessageType;

public class AnnotationLoader {

	public static Map<MGSMessageType, Class<?>> load(String packageName) {
		Map<MGSMessageType, Class<?>> commandRouting = new HashMap<>();
		Reflections reflections = new Reflections(packageName);
		Set<Class<?>> classes = reflections.getTypesAnnotatedWith(CommandProcessor.class);
		for (Class<?> clazz : classes) {
			CommandProcessor commands = clazz.getAnnotation(CommandProcessor.class);
			MGSMessageType type = commands.type();
			commandRouting.put(type, clazz);
		}
		return commandRouting;
	}
}
