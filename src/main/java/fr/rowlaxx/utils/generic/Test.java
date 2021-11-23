package fr.rowlaxx.utils.generic;

import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;

import fr.rowlaxx.utils.generic.bounds.BoundsResolver;
import fr.rowlaxx.utils.generic.destination.DestinationResolverUtils;

public class Test {

	public static void main(String[] args) {
		
		final Map<TypeVariable<?>,Class<?>[]> map = BoundsResolver.resolve(TestObject.Test2.class);
		
		
		System.out.println("Result : ");
		for (Entry<TypeVariable<?>,Class<?>[]> entry : map.entrySet())
			System.out.println(entry.getKey() + "\t" + Arrays.toString(entry.getValue()));
	}

}
