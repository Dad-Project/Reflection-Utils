package fr.rowlaxx.utils;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.Objects;

public class GenericUtils {
	
	private static final HashMap<TypeVariable<?>, HashMap<Class<?>, Class<?>>> resolved = new HashMap<>();
	
	public static Class<?> resolve(TypeVariable<?> typeVariable, Class<?> clazz){
		Objects.requireNonNull(typeVariable, "typeVariable may not be null.");
		Objects.requireNonNull(clazz, "clazz may not be null.");
		
		HashMap<Class<?>, Class<?>> map = resolved.get(typeVariable);
		if (map != null) {
			Class<?> c = map.get(clazz);
			if (c != null)
				return c;
		}
		else
			resolved.put(typeVariable, map = new HashMap<>());
		
		Class<?> resolved = resolveRec(typeVariable, clazz);
		map.put(clazz, resolved);
		return resolved;
	}
	
	private static Class<?> resolveRec(Type type, Class<?> clazz) {
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
					if (typevariables[i] == type) {
						if (generic[i] instanceof TypeVariable)
							return resolveRec((TypeVariable<?>)generic[i], clazz);
						else if (generic[i] instanceof Class)
							return (Class<?>)generic[i];
						else if (generic[i] instanceof ParameterizedType)
							return (Class<?>)((ParameterizedType) generic[i]).getRawType();
						else if (generic[i] instanceof GenericArrayType)
							return resolveRec(((GenericArrayType)generic[i]).getGenericComponentType(), clazz);
						else if (generic[i] instanceof WildcardType)
							return Object.class;
						else
							throw new GenericUtilsException("Type not supported : " + generic[i].getClass());
					}
				
			}
						
			temp = temp.getSuperclass();
		}
		
		throw new GenericUtilsException("Unable to resolve type " + type + " with class " + clazz);
	}
	

}
