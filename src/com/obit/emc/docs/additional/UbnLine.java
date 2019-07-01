package com.obit.emc.docs.additional;

/**
 * @author InferNix
 * @date: 26.05.2009
 */

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.general.*;

import java.sql.SQLException;
import java.sql.ResultSet;

import org.w3c.dom.Element;

import com.obit.emc.docs.Dictionaries.*;
import com.obit.emc.docs.additional.*;

public class UbnLine {

    public UbnLine(Element task, Context con, String kif, String operType, String estimateID, String memGroupCode,
                   Kbk kbk, String budgetID, String recipientID, String title, String id, String assign_amt1, String assign_amt2, String assign_amt3, String limit_amt1, String limit_amt2, String limit_amt3) throws UserException, SQLException {
        _kif = kif;
        _operTypeId = operType;
        if (estimateID != null && !estimateID.equals(""))
            _estimate = new Estimate(task, con, estimateID);
        if (memGroupCode != null && !memGroupCode.equals(""))
            _memGroupCode = memGroupCode;
        _kbk = kbk;
        _budgetID = budgetID;
        _recipientID = recipientID;
        _assign_amt1 = assign_amt1;
        _assign_amt2 = assign_amt2;
        _assign_amt3 = assign_amt3;
        _limit_amt1 = limit_amt1;
        _limit_amt2 = limit_amt2;
        _limit_amt3 = limit_amt3;
        if (title == null) _isHead = false;
        else
            _isHead = title.equals(id);
    }

    public boolean IsHead() {
        return _isHead;
    }

    public String GetKif() {
        return _kif;
    }

    public String GetOper() {
        return _operTypeId;
    }

    public Estimate GetEstimate() {
        return _estimate;
    }


    public String GetMemGroupCode() {
        return _memGroupCode;
    }

    public Kbk getKbk() {
        return _kbk;
    }

    public String getBudgetID() {
        return _budgetID;
    }

    public String getRecipientID() {
        return _recipientID;
    }

    public String get_assign_amt1() {
        return _assign_amt1;
    }

    public String get_assign_amt2() {
        return _assign_amt2;
    }

    public String get_assign_amt3() {
        return _assign_amt3;
    }

    public String get_limit_amt1() {
        return _limit_amt1;
    }

    public String get_limit_amt2() {
        return _limit_amt2;
    }

    public String get_limit_amt3() {
        return _limit_amt3;
    }

    //private Kbk kbk;
    private String _kif;
    private String _operTypeId;
    private Estimate _estimate = null;
    private String _memGroupCode = "";
    private Kbk _kbk;
    private String _budgetID = "";
    private String _recipientID = "";
    private boolean _isHead = false;
    private String _assign_amt1;
    private String _assign_amt2;
    private String _assign_amt3;
    private String _limit_amt1;
    private String _limit_amt2;
    private String _limit_amt3;

}
