package com.obit.emc.docs.Dictionaries;
import com.obit.emc.general.emcCustomRoot.*;


import org.w3c.dom.Element;
import com.bssys.server.Context;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by MakKN on 27.04.2016.
 */
public class Respperson {
    public Respperson(Element task, Context con, String orgID) throws SQLException {

        // test
        ArrayList resppersonBuh = new ArrayList();
        ArrayList resppersonHead = new ArrayList();

        resppersonBuh.addAll(Arrays.asList(19, -3, 33, 21, 35, 40, 48, 15, 14, 70, 71, 75, 61, 91, 94, 96));
        resppersonHead.addAll(Arrays.asList(-2, 1, 3, 4, 7, 8, 22, 23, 24, 34, 36, 37, 38, 39, 41, 43, 44, 45, 46, 26, 27,
                28, 29, 30, 33, 49, 50, 51, 52, 53, 54, 55, 56, 57, 60, 62, 63, 64, 65, 66, 69, 78, 84, 88, 90, 95));

        _org_buh_FIO_ar = getListFIO(con, getSQL(resppersonBuh, 1) + orgID);
        _org_head_FIO_ar = getListFIO(con, getSQL(resppersonHead, 1) + orgID);

        _org_buh_FIO_notActual = getListFIO(con, getSQL(resppersonBuh, 0) + orgID);
        _org_head_FIO_notActual = getListFIO(con, getSQL(resppersonHead, 0) + orgID);

        test.addAll(_org_buh_FIO_ar);
        test.addAll(_org_head_FIO_ar);
        test.addAll(_org_buh_FIO_notActual);
        test.addAll(_org_head_FIO_notActual);
    }

    public String getSQL(ArrayList list, int actual_flag) {
        String sql = "SELECT FIO, appointment_id, actual_flag from respperson where appointment_id IN (";
        for (int i = 0; i < list.size(); i++) {
            sql += list.get(i);
            if (i < list.size()-1) sql += " ,";
        }
        sql += ") AND ACTUAL_FLAG=" + actual_flag + " and org_id=";
        return sql;
    }

    public ArrayList getListFIO(Context con, String sql) throws SQLException {
        ArrayList temp = new ArrayList();

        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            temp.add(rs.getString("FIO"));
        }
        rs.close();
        return temp;
    }

    // ФИО ответственного из справочника ответственных лиц
    public ArrayList<String> _org_buh_FIO_ar = new ArrayList<String>();
    public ArrayList<String> _org_head_FIO_ar = new ArrayList<String>();
    public ArrayList<String> _org_buh_FIO_notActual = new ArrayList<String>();
    public ArrayList<String> _org_head_FIO_notActual = new ArrayList<String>();

    public ArrayList<String> test = new ArrayList<>();

}
