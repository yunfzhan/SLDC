package org.sldc.assist;

import java.util.Collection;
import java.util.Iterator;

public class ItemIterator implements Iterable<Object> {

	private Object obj = null;
	
	public ItemIterator(Object o) {
		obj = o;
	}
	
	@Override
	public Iterator<Object> iterator() {
		if(CSQLUtils.isArray(obj))
			return new ArrayIterator();
		else if(CSQLUtils.isCollection(obj)) {
			@SuppressWarnings("unchecked")
			Collection<Object> c = (Collection<Object>)obj;
			return c.iterator();
		}
		return null;
	}

	private class ArrayIterator implements Iterator<Object> {

		private int currentIndex = 0;
		
		@Override
		public boolean hasNext() {
			Object[] arr = (Object[])obj;
			if(currentIndex>=arr.length) return false;
			return true;
		}

		@Override
		public Object next() {
			Object[] arr = (Object[])obj;
			return arr[currentIndex++];
		}

		@Override
		public void remove() {
			
		}
		
	}
	
}
