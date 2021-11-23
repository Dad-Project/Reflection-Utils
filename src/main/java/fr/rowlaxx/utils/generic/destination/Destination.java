package fr.rowlaxx.utils.generic.destination;

import java.io.Serializable;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Destination<T>  {
	
	//Methodes statiques
	public static final <T> Destination<T> from(Class<T> clazz){
		return new Destination<T>(clazz);
	}

	public static final <T> Destination<T> from(Class<T> clazz, Class<?>... generics){
		return new Destination<T>(clazz, generics);
	}

	public static final <T> Destination<T> from(Class<T> clazz, Destination<?>... generics){
		return new Destination<T>(clazz, generics);
	}

	public static final <T> Destination<?>[] resolveClassBounds(Class<T> clazz) {
		//On résoud d'abord le type raw
		final TypeVariable<Class<T>>[] typeVariables = clazz.getTypeParameters();
		final Map<TypeVariable<Class<T>>, Destination<?>> resolved = resolveRawType(clazz);
		//TODO Résoudre les types génériques
		
		//On remet en ordre
		final Destination<?>[] result = new Destination<?>[typeVariables.length];
		for (int i = 0 ; i < result.length ; i++)
			result[i] = resolved.get( typeVariables[i] );		
		return result;
	}
	
	/**
	 * Résoud le raw type de tout les types variables d'une classe et et ses classes propriétaires si nécessaire
	 * @param <T> Le type de classe
	 * @param clazz La classe à résoudre 
	 * @return une map contenant en clé le TypeVariable et en valeur la destination
	 */
	//TODO code cleanup
	
	
	
	
	
	
	
	
	//Variables
	private final Class<T> clazz;
	private final Destination<?>[] generics;

	//Constructeurs
	private Destination(final Class<T> clazz, boolean raw) {
		this.clazz = Objects.requireNonNull(clazz, "clazz may not be null.");
		if (raw)
			this.generics = null;
		else {
			final int length = clazz.getTypeParameters().length;
			if (length == 0)
				this.generics = null;
			else
				this.generics = new Destination[length];
		}
	}
	
	/**
	 * Création d'une destination raw
	 * @param clazz la classe
	 */
	public Destination(final Class<T> clazz){
		this(clazz, true);
	}

	public Destination(final Class<T> clazz, Class<?>... generics) {
		this(clazz, toDestinationArray(generics));
	}

	private static Destination<?>[] toDestinationArray(Class<?>[] classes){
		final Destination<?>[] array = new Destination[classes.length];
		for (int i = 0 ; i < classes.length ; i++)
			array[i] = from(classes[i]);
		return array;
	}

	public Destination(final Class<T> clazz, Destination<?>... generics) {
		this.clazz = Objects.requireNonNull(clazz, "clazz may not be null.");
		Objects.requireNonNull(generics, "generics may not be null.");

		//On regarde s'il y a le bon nombre d'argument generique
		final Destination<?>[][] expectedTypes = resolveClassBounds(clazz);

		if (generics.length != expectedTypes.length)
			throw new DestinationException("The class " + clazz + " must have " + expectedTypes.length + " generic types.");
		
		this.generics = new Destination[expectedTypes.length];

		Destination<?> current;
		for (int i = 0 ; i < this.generics.length ; i++) {
			current = generics[i];
			Objects.requireNonNull(current, "array parameter generics has a null item at index " + i);
			
			for (Destination<?> expeted : expectedTypes[i])
				if (!current.isInnerclass(expeted))
					throw new DestinationException("The destination " + current + " must inherrit from " + expeted);
			
			this.generics[i] = generics[i];
		}
	}

	//Methodes
	/**
	 * Is this destination a raw type ?
	 * Exemple :
	 * String is a raw type
	 * List is a raw type
	 * List<String> is not a raw type
	 * @return true if this destination is a raw type
	 */
	public final boolean isRaw() {
		if (hasGenericParameters())
			return false;
		return clazz.getTypeParameters().length > 0;
	}
	
	public final Class<T> getDestinationClass() {
		return this.clazz;
	}
	
	public final Destination<?> getGenericParameter(int index) {
		checkNonRaw();
		return this.generics[index];
	}
	
	public final int getGenericParametersCount() {
		checkNonRaw();
		return this.generics.length;
	}
	
	/**
	 * This method return a copy of the internal array.
	 * For optimization, stock the copy somewhere or use getGenericParameter(int index) and getGenericParametersCount()
	 * @see getGenericParameter(int index)
	 * @see getGenericParametersCount()
	 * @return a copy of the generic parameter array. 
	 */
	public final Destination<?>[] getGenericParameters(){
		checkNonRaw();
		return Arrays.copyOf(this.generics, this.generics.length);
	}
	
	private final void checkNonRaw() {
		if (isRaw())
			throw new DestinationException("This object is a raw destination.");
	}

	public final boolean hasGenericParameters() {
		return this.generics != null;
	}

	public final boolean is(Class<?> clazz) {
		if ( !isRaw() )
			return false;
		return this.clazz == clazz;
	}

	public final boolean is(Destination<?> dest) {
		return equals(dest);
	}
	
	public final boolean isSuperclass(Class<?> clazz) {
		if (clazz == null)
			return false;
		return clazz.isAssignableFrom(this.clazz);
	}
	
	public final boolean isInnerclass(Class<?> clazz) {
		if (clazz == null)
			return false;
		return this.clazz.isAssignableFrom(clazz);
	}
	
	public final boolean isSuperclass(Destination<?> dest) {
		if (!isSuperclass(dest.clazz))
			return false;
		//TODO checker les parametres generiques
		return true;
	}
	
	public final boolean isInnerclass(Destination<?> dest) {
		if (dest == null)
			return false;
		return dest.isSuperclass(this);
	}
	
	public final boolean isInstance(Object object) {
		if (isRaw())
			return this.clazz.isInstance(object);
		//TODO vérifier les parametres generiques
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
		result = prime * result + Arrays.hashCode(generics);
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
		Destination<?> other = (Destination<?>) obj;
		if (clazz != other.clazz)
			return false;
		if (!Arrays.equals(generics, other.generics))
			return false;
		return true;
	}

	@Override
	public final String toString() {
		if (hasGenericParameters()) {
			final StringBuilder sb = new StringBuilder(128);

			sb.append(clazz.getName()).append('<');
			for (Destination<?> d : generics)
				sb.append(String.valueOf(d)).append(',');
			sb.setCharAt(sb.length()-1, '>');

			return sb.toString();
		}

		return clazz.getName();
	}
}
