package fr.rowlaxx.utils;

public class Test {

	public static void main(String[] args) {

		Class<?> c = GenericUtils.resolveClass(TestObject.class.getTypeParameters()[0], TestObject.De.class);

		System.out.println(c);
	}

}
