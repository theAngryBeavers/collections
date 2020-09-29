package map.impl;

import map.Map;

import java.util.ConcurrentModificationException;
import java.util.Objects;
import java.util.function.BiConsumer;

public class HashMap<K, V> implements Map<K, V> {

	static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16

	/**
	 * By default, the int data type is a 32-bit signed two's complement integer,
	 * which has a minimum value of -2^31 and a maximum value of (2^31)-1,
	 * ranges from –2,147,483,648 to 2,147,483,647.
	 *
	 * The first bit is reserved for the sign bit — it is 1 if the number is negative and 0 if it is positive.
	 * 1 << 30 is equal to 1,073,741,824
	 *
	 * it's two's complement binary integer is 01000000-00000000-00000000-00000000.
	 * 1 << 31 is equal to -2,147,483,648.
	 *
	 * It says the maximum size to which hash-map can expand is 1,073,741,824 = 2^30.
	 */
	static final int MAXIMUM_CAPACITY = 1 << 30;

	/**
	 * The load factor used when none specified in constructor.
	 */
	static final float DEFAULT_LOAD_FACTOR = 0.75f;

	/**
	 * Basic hash bin node.
	 */
	static class Node<K, V> implements Map.Entry<K, V> {

		final int hashCode;
		final K key;
		V value;
		Node<K, V> nextNode;

		public Node(int hashCode, K key, V value) {
			this(hashCode, key, value, null);
		}

		public Node(int hashCode, K key, V value, Node<K, V> nextNode) {
			this.key = key;
			this.value = value;
			this.nextNode = nextNode;
			this.hashCode = hashCode;
		}

		@Override
		public K getKey() {
			return key;
		}

		@Override
		public V getValue() {
			return value;
		}

		@Override
		public V setValue(V value) {
			V oldValue = this.value;
			this.value = value;
			return oldValue;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof Map.Entry)) return false;
			Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
			return Objects.equals(key, entry.getKey()) &&
					value.equals(entry.getValue());
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(key) ^ Objects.hashCode(value);
		}

		@Override
		public String toString() {
			return key.toString() +
					" = " +
					value.toString();
		}
	}

	/* ------------------ Static utilities -----------------*/

	static final int hash(Object key) {
		int h;
		return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
	}

	/**
	 * Returns a power of two size for the given target capacity.
	 */
	static final int bucketsSizeFor(int cap) {
		int n = cap - 1;
		n |= n >>> 1;
		n |= n >>> 2;
		n |= n >>> 4;
		n |= n >>> 8;
		n |= n >>> 16;
		return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
	}

	/* ---------------------- Fields ---------------------- */

	/**
	 * The number of key-value mappings contained in this map.
	 */
	private int size;

	private int threshold;

	private Node<K, V>[] buckets;

	final float loadFactor;

	private int numberOfModifications;

	/* ------------------- Constructors ------------------- */

	public HashMap(int initialCapacity, float loadFactor) {
		if (loadFactor <= 0 || Float.isNaN(loadFactor))
			loadFactor = DEFAULT_LOAD_FACTOR;
		if (initialCapacity < 0)
			initialCapacity = DEFAULT_INITIAL_CAPACITY;
		if (initialCapacity > MAXIMUM_CAPACITY)
			initialCapacity = MAXIMUM_CAPACITY;
		this.loadFactor = loadFactor;
		this.threshold = bucketsSizeFor(initialCapacity);
	}

	public HashMap(int initialCapacity) {
		this(initialCapacity, DEFAULT_LOAD_FACTOR);
	}

	public HashMap() {
		this.loadFactor = DEFAULT_LOAD_FACTOR;
	}

	/* ---------------------- Methods ---------------------- */

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public boolean containsValue(Object value) {
		Node<K, V>[] bucketsForIteration;
		V currentValue;
		if ((bucketsForIteration = buckets) != null && size > 0) {
			for (int i = 0; i < bucketsForIteration.length; i++) {
				for (Node<K, V> currentNode = bucketsForIteration[i];
				     currentNode != null;
				     currentNode = currentNode.nextNode) {
					if ((currentValue = currentNode.value) == value ||
							(value != null && value.equals(currentValue)))
						return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean containsKey(Object key) {
		return getNode(hash(key), key) != null;
	}

	@Override
	public V get(Object key) {
		Node<K, V> node = getNode(hash(key), key);
		return node == null ? null : node.value;
	}

	private Node<K, V> getNode(int hash, Object key) {
		Node<K, V>[] bucketsForIteration;
		Node<K, V> headNode;
		Node<K, V> currentNode;
		int bucketsForIterationLength;
		K currentKey;

		if ((bucketsForIteration = buckets) != null &&
				(bucketsForIterationLength = bucketsForIteration.length) > 0 &&
				(headNode = bucketsForIteration[(bucketsForIterationLength - 1) & hash]) != null) {
			if (headNode.hashCode == hash && // check headNode node to reduce complexity of search operation
					((currentKey = headNode.key) == key ||
							(key != null && key.equals(currentKey)))) {
				return headNode;
			} else if ((currentNode = headNode.nextNode) != null) {
				do {
					if (currentNode.hashCode == hash &&
							((currentKey = currentNode.key) == key ||
									(key != null && key.equals(currentKey)))) {
						return currentNode;
					}
				} while ((currentNode = currentNode.nextNode) != null);
			}
		}
		return null;
	}

	@Override
	public V put(K key, V value) {
		return putVal(hash(key), key, value);
	}

	private V putVal(int hash, K key, V value) {
		Node<K, V>[] bucketsForIteration;
		Node<K, V> nodePointer;
		int bucketsForIterationLength;
		int currentBucketIndex;

		if ((bucketsForIteration = buckets) == null ||
				(bucketsForIterationLength = bucketsForIteration.length) == 0) {
			bucketsForIterationLength = (bucketsForIteration = resize()).length;
		}

		if ((nodePointer =
				bucketsForIteration[currentBucketIndex = (bucketsForIterationLength - 1) & hash]) == null) {
			bucketsForIteration[currentBucketIndex] = new Node<>(hash, key, value, null);
		} else {
			Node<K, V> currentNode;
			K currentKey;

			if (nodePointer.hashCode == hash &&
					((currentKey = nodePointer.key) == key ||
							(key != null && key.equals(currentKey)))) {
				currentNode = nodePointer;
			} else {
				for (int nodeCount = 0; ; nodeCount++) {
					if ((currentNode = nodePointer.nextNode) == null) {
						nodePointer.nextNode = new Node(hash, key, value, null);
						break;
					}
					if (currentNode.hashCode == hash &&
							((currentKey = currentNode.key) == key ||
									(key != null && key.equals(currentKey))))
						break;
					nodePointer = currentNode;
				}
			}

			if (currentNode != null) { // existing mapping for key
				V oldValue = currentNode.value;
				currentNode.value = value;
				return oldValue;
			}
		}

		numberOfModifications++;

		if (++size > threshold) resize();

		return null;
	}

	public V remove(Object key) {
		Node<K, V> node = removeNode(hash(key), key);
		return node == null ? null : node.value;
	}

	@Override
	public void forEach(BiConsumer<? super K, ? super V> action) {
		Node<K, V>[] bucketsForIterations;
		if (action == null)
			throw new NullPointerException();
		if (size > 0 && (bucketsForIterations = buckets) != null) {
			int beforeOpNumberOfModifications = numberOfModifications;
			for (int i = 0; i < bucketsForIterations.length; ++i) {
				for (Node<K, V> headNode = bucketsForIterations[i]; headNode != null; headNode = headNode.nextNode)
					action.accept(headNode.key, headNode.value);
			}
			if (numberOfModifications != beforeOpNumberOfModifications)
				throw new ConcurrentModificationException();
		}
	}

	private Node<K, V> removeNode(int hash, Object key) {
		Node<K, V>[] bucketsForIteration;
		Node<K, V> headNode;
		int bucketsForIterationLength;
		int bucketIndex;

		if ((bucketsForIteration = buckets) != null &&
				(bucketsForIterationLength = bucketsForIteration.length) > 0 &&
				(headNode = bucketsForIteration[bucketIndex = (bucketsForIterationLength - 1) & hash]) != null) {
			Node<K, V> currentNode = null;
			Node<K, V> nodePointer;
			K currentKey;

			if (headNode.hashCode == hash &&
					((currentKey = headNode.key) == key || (key != null && key.equals(currentKey)))) {
				currentNode = headNode;
			} else if ((nodePointer = headNode.nextNode) != null) {
				do {
					if (nodePointer.hashCode == hash &&
							((currentKey = nodePointer.key) == key ||
									(key != null && key.equals(currentKey)))) {
						currentNode = nodePointer;
						break;
					}
					headNode = nodePointer;
				} while ((nodePointer = nodePointer.nextNode) != null);
			}
			if (currentNode != null) {
				if (currentNode == headNode) {
					bucketsForIteration[bucketIndex] = currentNode.nextNode;
				} else {
					headNode.nextNode = currentNode.nextNode;
				}
				numberOfModifications++;
				size--;
				return currentNode;
			}
		}
		return null;
	}

	@Override
	public void clear() {
		Node<K, V>[] bucketsForIteration;
		numberOfModifications++;
		if ((bucketsForIteration = buckets) != null && size > 0) {
			size = 0;
			for (int i = 0; i < bucketsForIteration.length; ++i)
				bucketsForIteration[i] = null;
		}
	}

	private Node<K, V>[] resize() {
		Node<K, V>[] oldBuckets = buckets;
		int oldCapacity = (oldBuckets == null) ? 0 : oldBuckets.length;
		int oldThreshold = threshold;
		int newCapacity = 0;
		int newThreshold = 0;

		if (oldCapacity > 0) {
			if (oldCapacity >= MAXIMUM_CAPACITY) {
				threshold = Integer.MAX_VALUE;
				return oldBuckets;
			} else if ((newCapacity = oldCapacity << 1) < MAXIMUM_CAPACITY &&
					oldCapacity >= DEFAULT_INITIAL_CAPACITY) {
				newThreshold = oldThreshold << 1; // 2x threshold
			}
		} else if (oldThreshold > 0) {
			newCapacity = oldThreshold;	// initial capacity was placed in threshold
		} else {               // zero initial threshold signifies using defaults
			newCapacity = DEFAULT_INITIAL_CAPACITY;
			newThreshold = (int)(DEFAULT_LOAD_FACTOR * newCapacity);
		}

		if (newThreshold == 0) {
			float ft = newCapacity * loadFactor;
			newThreshold = (newCapacity < MAXIMUM_CAPACITY && ft < (float) MAXIMUM_CAPACITY) ?
					(int) ft :
					Integer.MAX_VALUE;
		}

		threshold = newThreshold;
		@SuppressWarnings({"rawtypes","unchecked"})
		Node<K, V>[] newBuckets = (Node<K, V>[])new Node[newCapacity];
		buckets = newBuckets;
		if (oldBuckets != null) {
			for (int j = 0; j < oldCapacity; j++) {
				Node<K, V> node;
				if ((node = oldBuckets[j]) != null) {
					oldBuckets[j] = null;   // remove it
					if (node.nextNode == null) {
						newBuckets[node.hashCode & (newCapacity - 1)] = node;
					} else { // preserve old order
						Node<K, V> lowHead = null, lowTail = null;
						Node<K, V> highHead = null, highTail = null;
						Node<K, V> nextNode;
						do {
							nextNode = node.nextNode;
							if ((node.hashCode & oldCapacity) == 0) {
								if (lowTail == null) {
									lowHead = node;
								} else {
									lowTail.nextNode = node;
								}
								lowTail = node;
							} else {
								if (highTail == null) {
									highHead = node;
								} else {
									highTail.nextNode = node;
								}
								highTail = node;
							}
						} while ((node = nextNode) != null);

						if (lowTail != null) {
							lowTail.nextNode = null;
							newBuckets[j] = lowHead;
						}

						if (highTail != null) {
							highTail.nextNode = null;
							newBuckets[j + oldCapacity] = highHead;
						}
					}
				}
			}
		}
		return newBuckets;
	}

}
