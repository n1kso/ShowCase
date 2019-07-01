package com.obit.emc.checkers;

import com.obit.emc.Constants;

/**
 * Created with IntelliJ IDEA.
 * User: CherIA
 * Date: 09.08.13
 * Time: 11:54
 * To change this template use File | Settings | File Templates.
 */
public class UvsbChecker {
    /*
    Если в ЭД указано разрешение, но КВСР не заполнено, то возвращаем false;
     */
    public static Boolean Check_KADMR(String _permission_id, String _KADMR_code) {
        if (!_permission_id.equals("") && _KADMR_code.equals("0")) return false;
        return true;
    }
}