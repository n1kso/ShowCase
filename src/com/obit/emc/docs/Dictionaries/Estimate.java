/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.obit.emc.docs.Dictionaries;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.general.emcCustomDic;

import java.sql.SQLException;

import org.w3c.dom.Element;

/**
 * @author KanMA
 */
//Справочник бланков расходов
public class Estimate extends emcCustomDic {

    public Estimate(Element task, Context con, String id) throws UserException, SQLException {
        super(task, con, id);
        setMainSQL("", "estimate", "id=?", id);
        build();
    }

    public int getFsr_id() {
        return fsr_id;
    }

    public EstKind GetEstKind() {
        return _estKind;
    }

    public String GetOrgId() {
        return _org_id;
    }


    protected void getData() throws UserException, SQLException {
        fsr_id = mainRS.getInt("FINSOURCE_ID");
        _estKind = new EstKind(fTask, fcon, mainRS.getString("ESTKIND_ID"));
        _caption = mainRS.getString("CAPTION");
        _org_id = mainRS.getString("org_id");
    }

    private int fsr_id = -1;
    private EstKind _estKind;
    private String _caption;

    private String _org_id;

    public String getID() {
        return id;
    }

    public String getCaption() {
        return _caption;
    }
}
