/**
 * 
 */
package fr.hackchtx01.infra.util;

/**
 * Generic Builder
 * @param <T> : Object which is built by the implementation of Builder
 * @author yoan
 */
public interface GenericBuilder<T> {
    /**
     * Build a new Instance of T
     * @return T instance
     */
    public T build();
}
