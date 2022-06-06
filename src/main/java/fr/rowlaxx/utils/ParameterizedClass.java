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
	
	public static ParameterizedClass from(String str) throws ClassNotFoundException {
		str = str.trim();
		
		String firstPart = str.substring(0, str.indexOf('<')).trim();
		String lastPart = str.substring(str.indexOf('<')+1, str.indexOf('>')).trim();
	
		Class<?> rawType = Class.forName(firstPart);
		Type[] types = new Type[rawType.getTypeParameters().length];
		
		int index = 0, lastIndex;
		String part;
		for (int i = 0 ; i < types.length ; i++) {
			lastIndex = next(lastPart, index);
			part = lastPart.substring(index, lastIndex);
			types[i] = typeFrom(part);
			index = lastIndex;
		}
		
		return from(rawType, types);
	}
	
	private static int next(String str, int index) {
		int count = 0;
		for (int i = index + 1 ; i < str.length() ; i++) {
			if (str.charAt(i) == '<')
				count++;
			else if (str.charAt(i)== '>')
				count--;
			if (count == 0 && str.charAt(i) == ',')
				return i;
		}
		return -1;
	}
	
	private static Type typeFrom(String str) throws ClassNotFoundException {
		if (str.indexOf('<') == -1)
			return Class.forName(str);
		return from(str);
	}
	
	//Variables
	private final Class<?> rawClass;
	private final Type[] typeArguments;

	//Constructeurs
	protected ParameterizedClass(Class<?> rawClass, Type[] array) {
		Objects.requireNonNull(rawClass, "rawClass may not be null.");
		
		final TypeVariable<?>[] typeVariables = rawClass.getTypeParameters();
		if (typeVariables == null || typeVariables.length == 0)
			throw new IllegalArgumentException("rawClass is not a class that can be parameterized.");
	
		if (array.length != typeVariables.length)
			throw new IllegalArgumentException("array must have the same length as the number of type variables.");
		
		this.typeArguments = new Type[typeVariables.length];
		this.rawClass = ReflectionUtils.toWrapper(rawClass);
		
		for (int i = 0 ; i < array.length ; i++) {
			Objects.requireNonNull(array[i], "the element " + i + " of the array is null.");
			if (array[i] instanceof Class)
				this.typeArguments[i] = ReflectionUtils.toWrapper( (Class<?>) array[i]);
			else if (array[i] instanceof ParameterizedClass)
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(typeArguments);
		result = prime * result + Objects.hash(rawClass);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ParameterizedClass other = (ParameterizedClass) obj;
		return Objects.equals(rawClass, other.rawClass) && Arrays.equals(typeArguments, other.typeArguments);
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(128);
		sb.append(rawClass.getName());
		sb.append('<');
		for(int i = 0 ; i < typeArguments.length ; i++) {
			if (typeArguments[i] instanceof Class)
				sb.append(((Class<?>)typeArguments[i]).getName());
			else
				sb.append(((ParameterizedClass)typeArguments[i]).toString());
			sb.append(", ");
		}
		sb.replace(sb.length() - 2, sb.length(), ">");
		return sb.toString();
	}
}
