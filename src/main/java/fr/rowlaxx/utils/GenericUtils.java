package fr.rowlaxx.utils;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.Objects;

public class GenericUtils {
	
	private static final HashMap<Type, HashMap<Class<?>, Type>> resolved = new HashMap<>();
	
	public static Class<?> resolveClass(Type typeVariable, Class<?> clazz){
		Type result = resolve(typeVariable, clazz);
		if (result instanceof Class)
			return (Class<?>)result;
		if (result instanceof ParameterizedClass)
			return ((ParameterizedClass) result).getRawType();
		throw new GenericUtilsException("Unknow type.");
	}
	
	public static Type resolve(Type typeVariable, Class<?> clazz){
		Objects.requireNonNull(typeVariable, "typeVariable may not be null.");
		Objects.requireNonNull(clazz, "clazz may not be null.");
		
		HashMap<Class<?>, Type> map = resolved.get(typeVariable);
		Type type;
		if (map != null) {
			if ( (type = map.get(clazz)) != null)
				return type;
		}
		else
			resolved.put(typeVariable, map = new HashMap<>());
		
		final Type resolved = resolveRec(typeVariable, clazz);
		map.put(clazz, resolved);
		return resolved;
	}
	
	private static Type resolveRec(Type type, Class<?> clazz) {
		Objects.requireNonNull(type, "type may not be null.");
		
		//////////////////////////////////////////////////////////////////
		if (type instanceof Class || type instanceof ParameterizedClass)
			return type;
		//////////////////////////////////////////////////////////////////
		else if (type instanceof WildcardType)
			return Object.class;
		//////////////////////////////////////////////////////////////////
		else if (type instanceof GenericArrayType)
			return Object[].class;
		//////////////////////////////////////////////////////////////////
		else if (type instanceof ParameterizedType) {
			final Class<?> raw = (Class<?>) ((ParameterizedType)type).getRawType();
			final Type[] typeArgs = ((ParameterizedType)type).getActualTypeArguments();
			for (int j = 0 ; j < typeArgs.length ; j++)
				typeArgs[j] = resolveRec(typeArgs[j], clazz);
			return ParameterizedClass.from(raw, typeArgs);
		}
		//////////////////////////////////////////////////////////////////
		else if (type instanceof TypeVariable) {
			TypeVariable<?>[] typevariables;
			Type[] generic;
			Type superclass;
			
			Class<?> temp = clazz;
			while (temp != Object.class) {
				
				superclass = temp.getGenericSuperclass();
				
				if (superclass instanceof ParameterizedType)
				{
					generic = ((ParameterizedType) superclass).getActualTypeArguments();
					typevariables = ((Class<?>)((ParameterizedType) superclass).getRawType()).getTypeParameters();
					
					for (int i = 0 ; i < generic.length ; i++)
						if (typevariables[i] == type)
							return resolveRec(generic[i], clazz);
				}
							
				temp = temp.getSuperclass();
			}
			throw new GenericUtilsException("Unable to resolve type " + type + " with class " + clazz);
		}
		//////////////////////////////////////////////////////////////////
		else
			throw new GenericUtilsException("Type not supported : " + type.getClass());
		
	}
}