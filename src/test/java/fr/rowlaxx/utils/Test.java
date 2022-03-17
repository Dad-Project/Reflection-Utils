package fr.rowlaxx.utils;

import fr.rowlaxx.utils.GenericUtils;

public class Test {

	public static void main(String[] args) {

		Class<?> c = GenericUtils.resolve(TestObject.class.getTypeParameters()[0], TestObject.De.class);

		System.out.println(c);
	}

}
