package map;

import java.util.ConcurrentModificationException;
import java.util.Objects;
import java.util.function.BiConsumer;

public interface Map<K, V> {

	int size();

	boolean isEmpty();

	boolean containsKey(Object key);

	boolean containsValue(Object value);

	V get(Object key);

	V put(K key, V value);

	V remove(Object key);

	void forEach(BiConsumer<? super K, ? super V> action);

	void clear();

	interface Entry<K, V> {
		K getKey();
		V getValue();
		V setValue(V value);
	}
}
