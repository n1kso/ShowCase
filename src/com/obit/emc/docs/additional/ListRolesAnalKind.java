package com.obit.emc.docs.additional;

import java.util.ArrayList;
import java.util.Objects;

public class ListRolesAnalKind {
    private final static ArrayList<Key> keys;

    static {
        keys = new ArrayList<>();
        // ���� ����������: 18 - ���������, 19 - ����������
        // ��������� �������������: 0 - ���, 1- ������, 2 - �������, 3 - ���������

        keys.add(new Key("19", "19", "37"));
        keys.add(new Key("18", "18", "38"));
        keys.add(new Key("19", "18", "131"));
        keys.add(new Key("18", "19", "130"));
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