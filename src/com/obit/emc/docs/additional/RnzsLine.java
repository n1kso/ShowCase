package com.obit.emc.docs.additional;

/**
 * Created with IntelliJ IDEA.
 * User: CherIA
 * Date: 21.02.14
 * Time: 10:32
 * To change this template use File | Settings | File Templates.
 */
public class RnzsLine {
    public RnzsLine(String code) {
        _code = code;
    }

    public String GetCode() {
        return _code;
    }

    private String _code = "";
}
