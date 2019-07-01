package com.obit.emc.docs.additional;

import java.util.ArrayList;

//“ипы операций дл€ справок-уведомлений. ћожно использовать, если станет много
public class PayDetailOperationTypes {
    private final static ArrayList<Key> keys;

    static {
        keys = new ArrayList<>();

        keys.add(new Key("4", "19", "-28"));
        keys.add(new Key("18", "19", "-28"));
        keys.add(new Key("19", "18", "-28"));
    }

    public static boolean contains(Key key) {
        return keys.contains(key);
    }

    public static String recommendTypeOperation(Key key) {
        for (Key currentKey : keys) {
            if (currentKey.equalsWithoutTypeOperation(key)) {
                return currentKey.getThirdParameter();
            }
        }
        return null;
    }
}
