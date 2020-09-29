package collections;

public interface List<E> extends Iterable<E> {
	int size();

	boolean isEmpty();

	void clear();

	boolean add(int index, E value);

	boolean add(E value);

	boolean addAll(E[] array);

	boolean addAll(List<E> list);

	int indexOf(E value);

	boolean contains(E value);

	boolean remove(int index);

	E remove(E value);

	E get(int index);

	E element();

	E[] asArray();
}
