package fr.rowlaxx.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Objects;

public class ParameterizedClass implements ParameterizedType {

	//Methodes statiques
	public static ParameterizedClass from(Class<?> clazz, Type... typeArgument) {
		return new ParameterizedClass(clazz, typeArgument);
	}
	
	public static ParameterizedClass from(ParameterizedType type) {
		final Class<?> raw = (Class<?>) type.getRawType();
		final Type[] typeArgs = type.getActualTypeArguments();
		return new ParameterizedClass(raw, typeArgs);
	}
	
	//Variables
	private final Class<?> rawClass;
	private final Type[] typeArguments;

	//Constructeurs
	private ParameterizedClass(Class<?> rawClass, Type[] array) {
		Objects.requireNonNull(rawClass, "rawClass may not be null.");
		
		final TypeVariable<?>[] typeVariables = rawClass.getTypeParameters();
		if (typeVariables == null || typeVariables.length == 0)
			throw new IllegalArgumentException("rawClass is not a class that can be parameterized.");
	
		if (array.length != typeVariables.length)
			throw new IllegalArgumentException("array must have the same length as the number of type variables.");
		
		this.typeArguments = new Type[typeVariables.length];
		this.rawClass = rawClass;
		
		for (int i = 0 ; i < array.length ; i++) {
			Objects.requireNonNull(array[i], "the element " + i + " of the array is null.");
			if (array[i] instanceof Class || array[i] instanceof ParameterizedClass)
				this.typeArguments[i] = array[i];
			else if (array[i] instanceof ParameterizedType)
				this.typeArguments[i] = from((ParameterizedType)array[i]);
			else
				throw new IllegalStateException("unknow type : " + array[i].getClass());
		}
	}
	
	//Methodes
	public Type getActualTypeArgument(int index) {
		return typeArguments[index];
	}
	
	public int getActualTypeArgumentCount() {
		return typeArguments.length;
	}
	
	//Methodes réecrites
	@Override
	public Type[] getActualTypeArguments() {
		return Arrays.copyOf(typeArguments, typeArguments.length);
	}

	@Override
	public Class<?> getOwnerType() {
		return rawClass.getDeclaringClass();
	}

	@Override
	public Class<?> getRawType() {
		return rawClass;
	}
	
}
