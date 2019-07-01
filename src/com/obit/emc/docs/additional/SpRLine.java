package com.obit.emc.docs.additional;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import org.w3c.dom.Element;

import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: CherIA
 * Date: 26.04.13
 * Time: 12:46
 * To change this template use File | Settings | File Templates.
 */
public class SpRLine {

    private Element task;
    private Context con;

//    public SpRLine(Element task, Context con, Double Amount) throws UserException, SQLException {
//        _Amount = Amount;
//    }

    public SpRLine(Element task, Context con, String OperTypeId, String OrgAccId, BigDecimal amount) throws UserException {
        _operTypeId = OperTypeId;
        _orgAccId = OrgAccId;
        this.task = task;
        this.con = con;
        this.amount = amount;
    }

    private BigDecimal amount;
//    private Double _Amount;
    private String _operTypeId, _orgAccId;

//    public Double get_Amount() {
//        return _Amount;
//    }

    public String get_operTypeId() {
        return _operTypeId;
    }

    public String get_orgAccId() {
        return _orgAccId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
