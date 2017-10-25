package br.com.sysk.berkeleyQueue.util;

import java.io.IOException;
import java.lang.reflect.Field;

public class ReflectionsUtil {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Field getAnnotatedField(Class clazz, Class annotation) {
		if (!annotation.isAnnotation()) {
			throw new RuntimeException("parameter 'annotation' is not a Annotation Class");
		}
		for (Field field : clazz.getDeclaredFields()) {
			if (field.isAnnotationPresent(annotation)) {
				field.setAccessible(true);
				return field;
			}
		}
		throw new RuntimeException("Object doesn't have a attribute with @" + 
				annotation.getSimpleName() + " annotation");
	}
	
	@SuppressWarnings("rawtypes")
	public static Class getAnnotatedFieldClass(Object object, Class annotation) {
		Field field = getAnnotatedField(object.getClass(), annotation);
		return field.getDeclaringClass();
	}
	
	@SuppressWarnings("rawtypes")
	public static <T> T getAnnotatedFieldValue(Object data, Class annotation, Class<T> clazz) 
			throws IOException, IllegalArgumentException, IllegalAccessException { 
		Field field = getAnnotatedField(data.getClass(), annotation);
		Object dataValue = field.get(data);
		T value = JsonUtil.fromJson(JsonUtil.toJson(dataValue), clazz);
		return value;
	}
	
	public static void setFieldValue(Field field, Object data, Object value) 
			throws IllegalArgumentException, IllegalAccessException {
		if (value.getClass().equals(field.getDeclaringClass())) {
			field.setAccessible(true);
			field.set(data, value);
		}
	}
}
