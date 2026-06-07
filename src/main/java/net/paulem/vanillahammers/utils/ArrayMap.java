package net.paulem.vanillahammers.utils;

import java.util.*;

// TODO: Merge with Arcana
/**
 * Allow for easily using list of objects in a map, without having to instantiate everything each time and doing checking
 */
public class ArrayMap<K, V> {
    private final Map<K, List<V>> map;

    /**
     * Creates a new map with the default implementation (HashMap)
     */
    public ArrayMap() {
        this(new HashMap<>());
    }

    /**
     * Constructor that accepts an existing map (e.g., HashMap, TreeMap).
     * Ensures the map is not null to prevent NullPointerExceptions down the road.
     */
    public ArrayMap(Map<K, List<V>> map) {
        if (map == null) {
            throw new IllegalArgumentException("The underlying map cannot be null");
        }
        this.map = map;
    }

    /**
     * Adds a value to the list associated with the specified key.
     * If the key does not exist, the list is automatically created.
     * @return true if the value was successfully added.
     */
    public boolean put(K key, V value) {
        // computeIfAbsent initializes the ArrayList only if the key is not already present
        return map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
    }

    /**
     * Adds a collection of values to the specified key.
     */
    public boolean putAll(K key, Collection<? extends V> values) {
        if (values == null || values.isEmpty()) {
            return false;
        }
        return map.computeIfAbsent(key, k -> new ArrayList<>()).addAll(values);
    }

    /**
     * Retrieves the list associated with the key.
     * Returns an empty list (rather than null) if the key does not exist,
     * preventing crashes and null checks on the caller's side.
     */
    public List<V> get(K key) {
        return map.getOrDefault(key, new ArrayList<>());
    }

    /**
     * Removes a specific value from the list associated with the key.
     * If the list becomes empty after removal, the key is removed from the map to clean up.
     */
    public boolean removeValue(K key, V value) {
        List<V> list = map.get(key);
        if (list == null) {
            return false;
        }

        boolean removed = list.remove(value);

        // Clean up the map if the list is now empty
        if (list.isEmpty()) {
            map.remove(key);
        }

        return removed;
    }

    /**
     * Completely removes the key and its associated list.
     */
    public List<V> removeKey(K key) {
        return map.remove(key);
    }

    /**
     * Checks if the key exists and contains at least one element.
     */
    public boolean containsKey(K key) {
        return map.containsKey(key) && !map.get(key).isEmpty();
    }

    /**
     * Returns the set of keys.
     */
    public Set<K> keySet() {
        return map.keySet();
    }

    /**
     * Clears the underlying map.
     */
    public void clear() {
        map.clear();
    }

    /**
     * Returns the size of the map (the number of unique keys).
     */
    public int size() {
        return map.size();
    }

    /**
     * Get the values of the map.
     */
    public Collection<List<V>> values() {
        return map.values();
    }
}