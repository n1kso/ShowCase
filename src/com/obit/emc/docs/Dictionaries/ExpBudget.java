package com.obit.emc.docs.Dictionaries;

import com.bssys.server.Context;
import com.obit.emc.docs.additional.*;
import org.w3c.dom.Element;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: kanma
 * Date: 20.06.2012
 * Time: 12:56:56
 * To change this template use File | Settings | File Templates.
 */
public class ExpBudget {


    public boolean CheckExpBudget(Element task, Context con,UbnLine line) throws SQLException {
        String sql = "SELECT *  FROM Expbudget WHERE " +
                " KFSR_CODE = "      + line.getKbk().getKFSR_CODE()+
                " AND KCSR_CODE = " + line.getKbk().getKCSR_CODE()+
                " AND KVR_CODE = "   + line.getKbk().getKVR_CODE()+
                " AND KESR_CODE = "  + line.getKbk().getKESR_CODE()+
                " AND KADMR_CODE = " + line.getKbk().getKADMR_CODE()+
                " AND KDF_CODE = "   + line.getKbk().getKDF_CODE()+
                " AND KDE_CODE = "   + line.getKbk().getKDE_CODE()+
                " AND KDR_CODE = "   + line.getKbk().getKDR_CODE()+
                " AND ESTIMATE_ID = "+ line.GetEstimate().getID()+
                " AND BUDGET_ID = "  + line.getBudgetID()+
                " AND RECIPIENT_ID = "+ line.getRecipientID();
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) return false;

        int r = rs.getInt("MEMGROUP_CODE");
        if (r==0) return false; else return true;
    }
}
