package com.obit.emc.docs.additional;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import org.w3c.dom.Element;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: BelKA
 * Date: 30.07.13
 * Time: 16:08
 * To change this template use File | Settings | File Templates.
 */
public class SopsLine {
    public SopsLine(Element task, Context con, Double Ful, Double Amount, String Need) throws UserException, SQLException {
        _Ful = Ful;
        _Amount = Amount;
        _Need = Need;
    }

    private Double _Ful;
    private Double _Amount;
    private String _Need;

    public Double get_Ful() {
        return _Ful;
    }

    public Double get_Amount() {
        return _Amount;
    }

    public String get_Need() {
        return _Need;
    }
}