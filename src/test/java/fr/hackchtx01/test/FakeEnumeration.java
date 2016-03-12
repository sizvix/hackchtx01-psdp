/**
 * 
 */
package fr.hackchtx01.test;

import java.util.Enumeration;

/**
 * Fake java.util.Enumeration for test purpose only
 * @author yoan
 */
public class FakeEnumeration<T> implements Enumeration<T> {
	@Override
	public boolean hasMoreElements() {
		return false;
	}

	@Override
	public T nextElement() {
		return null;
	}
}
