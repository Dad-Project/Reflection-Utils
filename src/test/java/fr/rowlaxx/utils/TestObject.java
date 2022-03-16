package fr.rowlaxx.utils;

import java.io.Serializable;
import java.util.List;

import fr.rowlaxx.utils.GenericUtils;

public class TestObject<E> {

	public static class Be<F> extends TestObject<F> {
		
		
	}
	
	public static class De extends Be<String>{
		
	}
	
}
