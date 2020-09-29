package map.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class HashMapTest {

	private static final int MIN_TEST_INTEGER_VALUE = 0;
	private static final int MAX_TEST_INTEGER_VALUE = 1_000_000;

	private Map<Integer, Integer> map;
	private map.Map<Integer, Integer> testMap;

	@BeforeEach
	public void init() {
		map = new HashMap<>();
		testMap = new map.impl.HashMap<>();
		for (int i = MIN_TEST_INTEGER_VALUE; i < MAX_TEST_INTEGER_VALUE; i++) {
			map.put(i, i);
			testMap.put(i, i);
		}
	}

	@Test
	public void resizeAndGetTest() {
		//compare size of original HashMap with size of map.impl.HashMap
		assertEquals(map.size(), testMap.size());

		//Test get method of original HashMap with get method of map.impl.HashMap
		for (Integer key : map.keySet()) {
			assertEquals(map.get(key), testMap.get(key));
		}
	}

	@Test
	public void putTest() {
		for (int i = (MAX_TEST_INTEGER_VALUE / 5); i < (MAX_TEST_INTEGER_VALUE / 4); i++) {
			map.put(i, i << 1);
			testMap.put(i, i << 1);
		}

		//compare size of original HashMap with size of map.impl.HashMap
		assertEquals(map.size(), testMap.size());

		//Test get method of original HashMap with get method of map.impl.HashMap
		for (Integer key : map.keySet()) {
			assertEquals(map.get(key), testMap.get(key));
		}
	}

	@Test
	public void removeTest() {
		for (int i = MAX_TEST_INTEGER_VALUE; i < MAX_TEST_INTEGER_VALUE; i += 11) {
			map.remove(i);
			testMap.remove(i);
		}

		//compare size of original HashMap with size of map.impl.HashMap
		assertEquals(map.size(), testMap.size());

		//Test get method of original HashMap with get method of map.impl.HashMap
		for (Integer key : map.keySet()) {
			assertEquals(map.get(key), testMap.get(key));
		}
	}

	@Test
	public void containsKeyAndValueTest() {
		for (int i = MIN_TEST_INTEGER_VALUE; i < (MAX_TEST_INTEGER_VALUE / 20); i++) {
			assertEquals(map.containsKey(i), testMap.containsKey(i));
			assertEquals(map.containsValue(i), testMap.containsValue(i));
		}
	}

	@Test
	public void clearAndIsEmptyTest() {
		map.clear();
		testMap.clear();

		assertEquals(map.size(), testMap.size());

		assertEquals(map.isEmpty(), testMap.isEmpty());

		for (int i = MIN_TEST_INTEGER_VALUE; i < (MAX_TEST_INTEGER_VALUE / 20); i++) {
			assertEquals(map.containsKey(i), testMap.containsKey(i));
			assertEquals(map.containsValue(i), testMap.containsValue(i));
		}

	}

	@Test
	public void forEachTest() {
		int[] counter = new int[] { 0 };
		String[] expectedResult = new String[MAX_TEST_INTEGER_VALUE];
		String[] actualResult = new String[MAX_TEST_INTEGER_VALUE];

		map.forEach((key, value) -> {
			expectedResult[counter[0]] = key.toString() + " " + value.toString();
			counter[0] += 1;
		});

		counter[0] = 0;

		testMap.forEach((key, value) -> {
			actualResult[counter[0]] = key.toString() + " " + value.toString();
			counter[0] += 1;
		});

		assertTrue(Arrays.equals(expectedResult, actualResult));

		assertThrows(ConcurrentModificationException.class, () -> {
			testMap.forEach((key, value) -> {
				testMap.put(key << 2, (key + value) >> 2);
			});
		});
	}
}
