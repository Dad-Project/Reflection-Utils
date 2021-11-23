package fr.rowlaxx.utils.generic.bounds;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import fr.rowlaxx.utils.generic.destination.DestinationResolverException;

public class BoundsResolver {

	public static Map<TypeVariable<?>, Class<?>[]> resolve(GenericDeclaration genericDeclaration){
		final Instance instance = new Instance(genericDeclaration);
		instance.resolve();
		return instance.getResult();
	}

	private static class Instance {

		//Variables
		private final Map<TypeVariable<?>, Class<?>[]> resolved;
		private final List<TypeVariable<?>> unresolved;

		//Constructeurs
		private Instance(GenericDeclaration genericDeclaration) {
			Objects.requireNonNull(genericDeclaration, "genericDeclaration may not be null.");

			final TypeVariable<?>[] toSolve = genericDeclaration.getTypeParameters();
			this.unresolved = new ArrayList<>(toSolve.length);
			for (int i = 0 ; i < toSolve.length ; i++)
				unresolved.add(toSolve[i]);

			this.resolved = new HashMap<>(toSolve.length);
		}

		//Methodes
		public boolean isResolved() {
			return this.unresolved.isEmpty();
		}

		public Map<TypeVariable<?>, Class<?>[]> getResult(){
			if (!isResolved())
				throw new IllegalStateException("Not resolved yet.");
			return this.resolved;
		}

		//Methodes solving
		public synchronized void resolve() {
			if (isResolved())
				return;

			boolean solvedOne, success;
			TypeVariable<?> typeVariable;

			while (!isResolved()) {
				solvedOne = false;

				for (int i = 0 ; i < unresolved.size() ; i++ ) {
					typeVariable = unresolved.get(i);

					success = resolve(typeVariable);
					if(!success)
						continue;

					unresolved.remove(i--);
					solvedOne = true;
				}

				if (!solvedOne)
					throw new BoundsResolverException("Unable to solve at least one TypeVariable");

				solvedOne = false;
			}
		}	

		private boolean resolve(TypeVariable<?> typeVariable) {
			if (resolved.containsKey(typeVariable))
				return false;

			final Type[] bounds = typeVariable.getBounds();
			final ArrayList<Class<?>> rawBounds = new ArrayList<>(bounds.length);

			System.out.println("Precessing " + typeVariable);
			for (Type bound : bounds) {
				System.out.println("\t" + bound);
				if (bound instanceof Class)
					rawBounds.add((Class<?>)bound);
				else if (bound instanceof ParameterizedType) {
					rawBounds.add( (Class<?>) ((ParameterizedType) bound).getRawType() );
					findPotential((ParameterizedType) bound);
				}
				else if (bound instanceof TypeVariable) {
					if (resolved.containsKey(bound)) //Le type variable a déjà été resolut
						for (Class<?> clazz : resolved.get(bound))//On ajoute donc ses résolutions
							rawBounds.add(clazz);
					else {//Le type variable n'a pas été résolut
						//On l'ajout dans la liste d'attente de résolution si besoin
						findPotential( (TypeVariable<?>)bound );
						return false;
					}
				}
				else
					throw new DestinationResolverException("Unknow type : " + bound.getClass() );
			}

			final Class<?>[] result = rawBounds.toArray(new Class<?>[rawBounds.size()]);
			System.out.println("Resolved " + typeVariable);
			resolved.put(typeVariable, result);
			return true;
		}	
		
		private void findPotential_Index(Type type) {
			if (type instanceof ParameterizedType)
				findPotential((ParameterizedType)type);
			else if (type instanceof TypeVariable)
				findPotential((TypeVariable<?>)type);
			else if (type instanceof WildcardType)
				findPotential((WildcardType)type);
			else if (type instanceof GenericArrayType)
				findPotential((GenericArrayType)type);
			else if (!(type instanceof Class) )
				throw new DestinationResolverException("Unknow type : " + type.getClass() );
		}
		
		private void findPotential(ParameterizedType type) {
			System.out.println("\t\tChecking ParameterizedType " + type);
			for (Type t : type.getActualTypeArguments())
				findPotential_Index(t);
		}
		
		private void findPotential(TypeVariable<?> type) {
			System.out.println("\t\tChecking TypeVariable " + type);
			if (!unresolved.contains(type)) {
				System.out.println("\t\t\tNeed to be resolved.");
				unresolved.add((TypeVariable<?>) type);
			}
		}
		
		private void findPotential(WildcardType type) {
			System.out.println("\t\tChecking WildcardType " + type);
			for (Type t : type.getLowerBounds())
				findPotential_Index(t);
			for (Type t : type.getUpperBounds())
				findPotential_Index(t);
		}
		
		private void findPotential(GenericArrayType type) {
			System.out.println("\t\tChecking GenericArrayType " + type);
			findPotential_Index(type.getGenericComponentType());
		}
	}
}