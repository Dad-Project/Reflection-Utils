package fr.rowlaxx.utils.generic.destination;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
	
	public static final <T> Destination<T> parse(String string) throws ClassNotFoundException {
		return parse(string, 0, string.length());
	}
	
	@SuppressWarnings("unchecked")
	private static final <T> Destination<T> parse(String string, int start, int end) throws ClassNotFoundException {
		final int startGeneric = string.indexOf('<', start);
		if (startGeneric == -1)
			return (Destination<T>) from(Class.forName(string));
		
		final String base = string.substring(start, startGeneric).trim();
		final Builder<T> builder = (Builder<T>) newBuilder(Class.forName(base));
		
		int temp = startGeneric+1, temp2;
		while(true) {
			temp2 = next(string, temp);
			if (temp2 > end || temp2 >= string.length()) {
				builder.addGeneric(parse(string, temp, end));
				break;
			}
			builder.addGeneric(parse(string, temp, temp2));
			temp = temp2 + 1;
		}
		
		return builder.build();
	}
	
	private static final int next(String string, int start) {
		int t = 0;
		for (int i = start ; i < string.length() ; i++)
		{
			if (string.charAt(i) == '<')
				t++;
			if (string.charAt(i) == '>')
				t--;
			if (string.charAt(i) == ',' && t == 0)
				return i;
		}
		return string.length();
	}
	
	//Builders
	public static <T> Builder<T> newBuilder(Class<T> clazz){
		return new Builder<T>(clazz);
	}
	
	public static class Builder<T> {
		
		//Variables
		private final Class<T> clazz;
		private List<Destination<?>> generics = null;
		
		//Constructeurs
		private Builder(Class<T> clazz) {
			this.clazz = Objects.requireNonNull(clazz, "clazz may not be null.");
		}
		
		//Methodes
		public Builder<T> addGeneric(Class<?> clazz) {
			return addGeneric(from(clazz));
		}
		
		public Builder<T> addGeneric(Destination<?> destination) {
			Objects.requireNonNull(destination, "destination may not be null.");
			if (getGenericCount() == getMaximumGenericCount())
				throw new DestinationException("The class " + clazz + " only have " + getMaximumGenericCount() + " type parameters.");
			if (generics == null)
				this.generics = new ArrayList<>();
			this.generics.add(destination);
			return this;
		}
		
		public Builder<T> addGeneric(String destination) throws ClassNotFoundException{
			return addGeneric(parse(destination));
		}
		
		public int getGenericCount() {
			if (generics==null)
				return 0;
			return generics.size();
		}
		
		public int getMaximumGenericCount() {
			return clazz.getTypeParameters().length;
		}
		
		public Destination<T> build(){
			if (generics == null)
				return new Destination<>(clazz);
			
			Destination<?>[] dest = new Destination<?>[generics.size()];
			generics.toArray(dest);
			return new Destination<T>(clazz, dest);
		}
	}
	
	//Variables
	private final Class<T> clazz;
	private final Destination<?>[] generics;
	private final int genericsCount;

	//Constructeurs
	private Destination(final Class<T> clazz) {
		this.clazz = Objects.requireNonNull(clazz, "clazz may not be null.");
		this.generics = null;
		this.genericsCount = clazz.getTypeParameters().length;
	}

	private Destination(final Class<T> clazz, Class<?>... generics) {
		this(clazz, toDestinationArray(generics));
	}

	private static Destination<?>[] toDestinationArray(Class<?>[] classes){
		final Destination<?>[] array = new Destination[classes.length];
		for (int i = 0 ; i < classes.length ; i++)
			array[i] = from(classes[i]);
		return array;
	}

	private Destination(final Class<T> clazz, Destination<?>... generics) {
		this.clazz = Objects.requireNonNull(clazz, "clazz may not be null.");
		this.genericsCount = clazz.getTypeParameters().length;
		
		if (generics == null)
			this.generics = null;
		else if (generics.length == genericsCount) {
			this.generics = new Destination<?>[genericsCount];
			for(int i = 0 ; i < genericsCount ; i++)
				this.generics[i] = Objects.requireNonNull(generics[i], "generics[" + i + "] may not be null.");
		}
		else
			throw new DestinationException("The class " + clazz + " must have " + genericsCount + " generic types.");
	}

	//Methodes
	public final boolean isRaw() {
		if (hasGenericParameters())
			return false;
		return genericsCount > 0;
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
	 * For optimization, store the copy somewhere or use getGenericParameter(int index) and getGenericParametersCount()
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
		return this.clazz == clazz;
	}

	public final boolean is(Destination<?> dest) {
		return equals(dest);
	}
	
	public final boolean isInstance(Object object) {
		return this.clazz.isInstance(object);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
		result = prime * result + Arrays.hashCode(generics);
		result = prime * result + genericsCount;
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
		if (clazz == null) {
			if (other.clazz != null)
				return false;
		} else if (!clazz.equals(other.clazz))
			return false;
		if (!Arrays.equals(generics, other.generics))
			return false;
		if (genericsCount != other.genericsCount)
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
