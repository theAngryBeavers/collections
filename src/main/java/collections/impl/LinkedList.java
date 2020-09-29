package collections.impl;

import collections.List;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

public class LinkedList<E> implements List<E>, Iterable<E> {

	static class ListNode<E> {
		ListNode prevElem;
		ListNode nextElem;
		E value;

		ListNode() {
			value = null;
			nextElem = null;
			prevElem = null;
		}

		ListNode(E value, ListNode<E> prevElem, ListNode<E> nextElem) {
			this.value = value;
			this.nextElem = nextElem;
			this.prevElem = prevElem;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof ListNode)) return false;
			ListNode<?> listNode = (ListNode<?>) o;
			return Objects.equals(prevElem, listNode.prevElem) &&
					Objects.equals(nextElem, listNode.nextElem) &&
					Objects.equals(value, listNode.value);
		}

		@Override
		public int hashCode() {
			return Objects.hash(prevElem, nextElem, value);
		}

		@Override
		public String toString() {
			return value.toString();
		}
	}

	private int size;
	private ListNode<E> head;
	private ListNode<E> tail;

	public LinkedList() {
		size = 0;
		head = null;
		tail = null;
	}

	public LinkedList(E[] array) {
		addAll(array);
	}

	public LinkedList(List<E> list) {
		addAll(list);
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0 &&
				head == null &&
				tail == null;
	}

	@Override
	public void clear() {
		size = 0;
		head = null;
		tail = null;
	}

	@Override
	public boolean add(int index, E value) {
		if (size >= 0 && index >= 0 && index <= size) {
			if (index == 0) {
				return push(value);
			} else if (index == size) {
				return add(value);
			} else {
				ListNode<E> pointer = head;
				for (int i = 0; i != index; i++) {
					pointer = pointer.nextElem;
				}
				ListNode<E> temp = new ListNode<>(value, pointer.prevElem, pointer);
				pointer.prevElem.nextElem = temp;
				pointer.prevElem = temp;
				size++;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean add(E value) {
		if (size == 0) {
			return push(value);
		} else if (size > 0) {
			tail.nextElem = new ListNode(value, tail, null);
			tail = tail.nextElem;
			size++;
			return true;
		}
		return false;
	}

	private boolean push(E value) {
		if (size == 0) {
			head = new ListNode<>(value, null, null);
			tail = head;
			size++;
			return true;
		} else if (size > 0) {
			ListNode<E> temp = new ListNode<>(value, null, head);
			head.prevElem = temp;
			head = temp;
			size++;
			return true;
		}
		return false;
	}

	@Override
	public boolean addAll(List<E> list) {
		return addAll(list.asArray());
	}

	@Override
	public boolean addAll(E[] array) {
		if (size == 0 && array != null) {
			head = new ListNode<>(array[0], null, null);
			ListNode<E> temp = head;
			for (int i = 1; i < array.length; i++) {
				temp.nextElem = new ListNode(array[i], temp, null);
				temp = temp.nextElem;
			}
			tail = temp;
			size = array.length;
			return true;
		} else if (size > 0 && array != null) {
			for (int i = 0; i < array.length; i++) {
				tail.nextElem = new ListNode(array[i], tail, null);
				tail = tail.nextElem;
			}
			size += array.length;
			return true;
		}
		return false;
	}

	@Override
	public int indexOf(E value) {
		if (size > 0) {
			int position = 0;
			ListNode<E> temp = head;
			while (temp != null) {
				if (value.equals(temp.value)) return position;
				temp = temp.nextElem;
				position++;
			}
		}
		return -1;
	}

	@Override
	public boolean contains(E value) {
		return indexOf(value) != -1;
	}

	@Override
	public E remove(E value) {
		int indexOfValue = indexOf(value);
		if (indexOfValue != -1) {
			return remove(indexOfValue) ? value : null;
		}
		return null;
	}

	@Override
	public boolean remove(int index) {
		if (size > 0 && index >= 0 && index < size) {
			if (index == size - 1) {
				tail = tail.prevElem;
				tail.nextElem = null;
			} else if (index == 0) {
				head = head.nextElem;
				head.prevElem = null;
			} else {
				int counter;
				ListNode<E> pointer = head;
				ListNode<E> temp;
				for (counter = 0; counter != index; counter++) {
					pointer = pointer.nextElem;
				}
				temp = pointer.nextElem;
				temp.prevElem = pointer.prevElem;
				pointer.prevElem.nextElem = temp;
			}
			size--;
			return true;
		}
		return false;
	}

	@Override
	public E get(int index) {
		if (size > 0 && index >= 0 && index < size) {
			if (index == size - 1) {
				return tail.value;
			} else if (index == 0) {
				return head.value;
			} else {
				ListNode<E> temp = head;
				for (int i = 0; i != index; i++) {
					temp = temp.nextElem;
				}
				return temp.value;
			}
		}
		return null;
	}

	@Override
	public E element() {
		return head.value;
	}

	@Override
	public E[] asArray() {
		if (size > 0) {
			E[] result = (E[]) new Object[size];
			ListNode<E> temp = head;
			for (int i = 0; i < size; i++) {
				result[i] = temp.value;
				temp = temp.nextElem;
			}
			return result;
		} else {
			return null;
		}
	}

	@Override
	public Iterator<E> iterator() {
		return new Iterator<E>() {
			ListNode<E> currentPosition = head;

			@Override
			public boolean hasNext() {
				return currentPosition != null;
			}

			@Override
			public E next() {
				E value = currentPosition.value;
				currentPosition = currentPosition.nextElem;
				return value;
			}
		};
	}

	@Override
	public void forEach(Consumer<? super E> action) {
		if (size > 0) {
			ListNode<E> currentPosition = head;
			while (currentPosition != null) {
				action.accept(currentPosition.value);
				currentPosition = currentPosition.nextElem;
			}
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof LinkedList)) return false;
		LinkedList<?> that = (LinkedList<?>) o;
		return size == that.size &&
				Objects.equals(head, that.head) &&
				Objects.equals(tail, that.tail);
	}

	@Override
	public int hashCode() {
		return Objects.hash(size, head, tail);
	}
}
