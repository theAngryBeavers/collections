package collections.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class LinkedListTest {

	private Integer[] testArray1 = new Integer[] {
			1, 2, 4, 45, 12, 2, 2, 1, 4, 34, 88, -1999, 129, 0, 34, -1999, 834, 500, 199, 32, 0, 11
	};

	private Integer[] testArray2 = new Integer[] {
			86, 99, 13, 55, -666, 9282, 1000000, -1, 0, 12, -1999, 11
	};

	private LinkedList<Integer> linkedListForTest;

	@BeforeEach
	public void init() {
		linkedListForTest = new LinkedList<>(testArray1);
	}

	@Test
	public void addTest() {
		linkedListForTest.add(-66666);
		linkedListForTest.add(66666);

		assertEquals(testArray1.length + 2, linkedListForTest.size());

		ArrayList<Integer> expectedValue = new ArrayList<>(Arrays.asList(testArray1));
		expectedValue.add(-66666);
		expectedValue.add(66666);

		assertTrue(Arrays.equals(expectedValue.toArray(new Integer[0]), linkedListForTest.asArray()));
	}

	@Test
	public void addAllFromArrayTest() {
		linkedListForTest.addAll(testArray2);
		ArrayList<Integer> expectedValue = new ArrayList<>(Arrays.asList(testArray1));
		expectedValue.addAll(Arrays.asList(testArray2));

		assertEquals(expectedValue.size(), linkedListForTest.size());
		assertTrue(Arrays.equals(expectedValue.toArray(new Integer[0]), linkedListForTest.asArray()));
	}

	@Test
	public void addAllFromListTest() {
		linkedListForTest.addAll(new LinkedList<>(testArray2));
		ArrayList<Integer> expectedValue = new ArrayList<>(Arrays.asList(testArray1));
		expectedValue.addAll(Arrays.asList(testArray2));

		assertEquals(expectedValue.size(), linkedListForTest.size());
		assertTrue(Arrays.equals(expectedValue.toArray(new Integer[0]), linkedListForTest.asArray()));
	}

	@Test
	public void containsTest() {
		linkedListForTest.addAll(testArray2);
		ArrayList<Integer> values = new ArrayList<>(Arrays.asList(testArray1));
		values.addAll(Arrays.asList(testArray2));

		for (Integer value : values) {
			assertTrue(linkedListForTest.contains(value));
		}
	}

	@Test
	public void indexOfTest() {
		linkedListForTest.addAll(testArray2);
		ArrayList<Integer> values = new ArrayList<>(Arrays.asList(testArray1));
		values.addAll(Arrays.asList(testArray2));

		for (Integer value : values) {
			assertEquals(values.indexOf(value), linkedListForTest.indexOf(value));
		}
	}

	@Test
	public void removeByIndexTest() {
		int[] indexes = new int[] {
				0, 12, 9, 7, 3, 26, 100
		};
		ArrayList<Integer> expected = new ArrayList<>(Arrays.asList(testArray1));
		for (int index : indexes) {
			try {
				expected.remove(index);
				linkedListForTest.remove(index);
			} catch (IndexOutOfBoundsException ex) {
				//NOP
			}
		}

		assertTrue(Arrays.equals(expected.toArray(new Integer[0]), linkedListForTest.asArray()));
	}

	@Test
	public void removeByValueTest() {
		ArrayList<Integer> expected = new ArrayList<>(Arrays.asList(testArray1));
		for (Integer value : testArray2) {
			try {
				expected.remove(value);
				linkedListForTest.remove(value);
			} catch (IndexOutOfBoundsException ex) {
				//NOP
			}
		}
	}

	@Test
	public void elementTest() {
		assertEquals(testArray1[0], linkedListForTest.element());
	}

	@Test
	public void get() {
		int[] indexes = new int[] {
				0, 12, 9, 7, 3, 21
		};
		ArrayList<Integer> expected = new ArrayList<>(Arrays.asList(testArray1));

		for (int index : indexes) {
			assertEquals(expected.get(index), linkedListForTest.get(index));
		}
	}

	@Test
	public void asArrayAndClearTest() {
		linkedListForTest.clear();
		assertEquals(0, linkedListForTest.size());
		for (Integer value : testArray2) {
			linkedListForTest.add(value);
		}
		assertTrue(Arrays.equals(testArray2, linkedListForTest.asArray()));
	}

	@Test
	public void removeFromEmptyListByIndex() {
		linkedListForTest.clear();

		assertEquals(0, linkedListForTest.size());
		assertFalse(linkedListForTest.remove(12));
	}

	@Test
	public void removeFromEmptyListByValue() {
		linkedListForTest.clear();

		assertEquals(0, linkedListForTest.size());
		assertEquals(null, linkedListForTest.remove(new Integer(18)));
	}

	@Test
	public void addToEmptyList() {
		int[] values = new int[] {
				0, 12, 9, 7, 3, 21
		};
		linkedListForTest.clear();
		ArrayList<Integer> expected = new ArrayList<>();
		for (int value : values) {
			linkedListForTest.add(value);
			expected.add(value);
		}

		assertTrue(Arrays.equals(expected.toArray(new Integer[0]), linkedListForTest.asArray()));
	}

	@Test
	public void addByIndexTest() {
		ArrayList<Integer> expected = new ArrayList<>(Arrays.asList(testArray1));
		for (Integer value : testArray2) {
			expected.add(12, value);
			linkedListForTest.add(12, value);
		}

		assertTrue(Arrays.equals(expected.toArray(new Integer[0]), linkedListForTest.asArray()));

		expected.clear();
		linkedListForTest.clear();

		for (Integer value : testArray2) {
			expected.add(0, value);
			linkedListForTest.add(0, value);
		}

		assertTrue(Arrays.equals(expected.toArray(new Integer[0]), linkedListForTest.asArray()));

		expected.addAll(Arrays.asList(testArray1));
		linkedListForTest.addAll(testArray1);
		for (Integer value : testArray2) {
			linkedListForTest.add(22, value);
			expected.add(22, value);
		}

		assertTrue(Arrays.equals(expected.toArray(new Integer[0]), linkedListForTest.asArray()));

	}

	@Test
	public void sizeTest() {
		assertEquals(testArray1.length, linkedListForTest.size());
	}
}
