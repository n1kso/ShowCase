package com.obit.emc.docs.additional;

import java.util.HashSet;
import java.util.Set;

public class ListContracts {

    private final static Set<Key> set;

    static {

        set = new HashSet<>();

        set.add(new Key("221", "244", "9"));
        set.add(new Key("222", "244", "9"));
        set.add(new Key("223", "244", "9"));
        set.add(new Key("224", "244", "9"));
        set.add(new Key("225", "244", "9"));
        set.add(new Key("226", "244", "9"));
        set.add(new Key("228", "244", "9"));
        set.add(new Key("296", "244", "9"));
        set.add(new Key("297", "244", "9"));
        set.add(new Key("298", "244", "9"));
        set.add(new Key("299", "244", "9"));
        set.add(new Key("310", "244", "9"));
        set.add(new Key("340", "244", "9"));
        set.add(new Key("342", "244", "9"));
        set.add(new Key("343", "244", "9"));
        set.add(new Key("344", "244", "9"));
        set.add(new Key("345", "244", "9"));
        set.add(new Key("346", "244", "9"));
        set.add(new Key("347", "244", "9"));
        set.add(new Key("349", "244", "9"));
        set.add(new Key("226", "407", "6"));
        set.add(new Key("310", "407", "6"));
        set.add(new Key("340", "407", "6"));
    }

    public static boolean contains(Key key) {
        return set.contains(key);
    }
}
