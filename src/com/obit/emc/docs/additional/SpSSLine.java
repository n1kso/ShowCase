package com.obit.emc.docs.additional;

/**
 * Created with IntelliJ IDEA.
 * User: CherIA
 * Date: 19.03.13
 * Time: 11:04
 * To change this template use File | Settings | File Templates.
 */

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.general.*;

import java.sql.SQLException;
import java.sql.ResultSet;

import org.w3c.dom.Element;

import com.obit.emc.docs.Dictionaries.*;
import com.obit.emc.docs.additional.*;

public class SpSSLine {

    public SpSSLine(Element task, Context con, Double Fin, Double Expense, String id, String title) throws UserException, SQLException {
        _Fin = Fin;
        _Expense = Expense;
        _id = id;
        if (title == null) _isHead = false;
        else
            _isHead = title.equals(id);
    }

    private Double _Fin;
    private Double _Expense;
    private String _id = "0";
    private Boolean _isHead;

    public Double get_Fin() {
        return _Fin;
    }

    public Double get_Expense() {
        return _Expense;
    }

    public boolean IsHead() {
        return _isHead;
    }
}
