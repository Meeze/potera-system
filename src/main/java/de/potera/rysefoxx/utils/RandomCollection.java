package de.potera.rysefoxx.utils;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;

public class RandomCollection<E> extends Object {
    public final NavigableMap<Double, E> map;
    private final Random random;
    private double total;

    public RandomCollection() {
        this(new Random());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public RandomCollection(Random random) {
        this.map = new TreeMap();
        this.total = 0.0D;
        this.random = random;
    }

    public void add(double weight, E result) {
        if (weight <= 0.0D) {
            return;
        }
        this.total += weight;
        this.map.put(Double.valueOf(this.total), result);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public WinningObject<E> next() {
        double value = this.random.nextDouble() * this.total;
        return new WinningObject(this.map.ceilingEntry(Double.valueOf(value)).getValue(), value);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public RandomCollection<E> clone() {
        try {
            return (RandomCollection) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void clear() {
        this.map.clear();
        this.total = 0.0D;
    }

    public static class WinningObject<E> extends Object {
        public E entry;
        public double ticket;

        public WinningObject(E e, double d) {
            this.entry = e;
            this.ticket = d;
        }
    }
}
