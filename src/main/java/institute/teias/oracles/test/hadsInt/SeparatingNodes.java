package institute.teias.oracles.test.hadsInt;

import institute.teias.utils.Pair;

import java.util.HashMap;

public class SeparatingNodes<K extends Comparable<K>, V> {
    private final HashMap<Pair<K, K>, V> pairs = new HashMap<>();

    private Pair<K, K> makeKey(K k1, K k2) {
        if (k1.compareTo(k2) < 0) return new Pair<>(k1, k2);
        return new Pair<>(k2, k1);
    }

    public void insertPair(K k1, K k2, V v) {
        Pair<K, K> key = this.makeKey(k1, k2);
        this.pairs.put(key, v);
    }

    public V checkPair(K k1, K k2) {
        Pair<K, K> key = this.makeKey(k1, k2);
        return this.pairs.get(key);
    }
}
