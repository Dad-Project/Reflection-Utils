package fr.rowlaxx.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectionUtils {

	public static Class<?> toWrapper(Class<?> clazz){
		if (clazz == void.class)
			return Void.class;
		if (clazz == int.class)
			return Integer.class;
		if (clazz == byte.class)
			return Byte.class;
		if (clazz == short.class)
			return Short.class;
		if (clazz == long.class)
			return Long.class;
		if (clazz == float.class)
			return Float.class;
		if (clazz == double.class)
			return Double.class;
		if (clazz == boolean.class)
			return Boolean.class;
		return clazz;
	}
	
	public static List<Method> getAllMethods(Class<?> clazz){
		final List<Method> list = new ArrayList<>(10);

		do {
			for(Method e : clazz.getDeclaredMethods())
				list.add(e);
		}while ((clazz = clazz.getSuperclass()) != null);

		return list;
	}

	public static List<Method> getAllMethods(Class<?> clazz, Class<? extends Annotation> annotation){
		final List<Method> list = new ArrayList<>(10);

		do {
			for(Method e : clazz.getDeclaredMethods())
				if (e.isAnnotationPresent(annotation))
					list.add(e);
		}while ((clazz = clazz.getSuperclass()) != null);

		return list;
	}
	
	public static List<Field> getAllFields(Class<?> clazz){
		final List<Field> list = new ArrayList<>(10);

		do {
			for(Field e : clazz.getDeclaredFields())
				list.add(e);
		}while ((clazz = clazz.getSuperclass()) != null);

		return list;
	}
	
	public static List<Field> getAllFields(Class<?> clazz, Class<? extends Annotation> annotation){
		final List<Field> list = new ArrayList<>(10);

		do {
			for(Field e : clazz.getDeclaredFields())
				if(e.isAnnotationPresent(annotation))
					list.add(e);
		}while ((clazz = clazz.getSuperclass()) != null);

		return list;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T tryGet(Field field, Object accessor) {
		try {
			field.setAccessible(true);
			return (T) field.get(accessor);
		}catch(IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void trySet(Field field, Object accessor, Object value) {
		field.setAccessible(true);
		try {
			field.set(accessor, value);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T tryInstanciate(Class<T> clazz, Object... params) {
		for (Constructor<?> constructor : clazz.getConstructors()) {
			if (constructor.getParameterCount() != params.length)
				continue;
			
			try {
				return (T) constructor.newInstance(params);
			} catch (InstantiationException e) {
				continue;
			} catch (IllegalAccessException e) {
				continue;
			} catch (IllegalArgumentException e) {
				continue;
			} catch (InvocationTargetException e) {
				continue;
			}
		}
		return null;
	}

}
