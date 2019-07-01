package com.obit.emc.docs.Dictionaries;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.general.emcCustomDic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.w3c.dom.Element;

import java.sql.SQLException;

/**
 * Created by MakKN on 22.05.2017.
 */
public class Purposefulgrant extends emcCustomDic {

    private String purposeFulGrantSourceID;
    private String purposeFulGrantSourceName;

    public Purposefulgrant(Element task, Context con, String id) throws UserException, SQLException {
        super(task, con, id);
        setMainSQL("","purposefulgrant","id=?",id);
        build();
    }
    @Override
    protected void getData() throws UserException, SQLException {
        code = mainRS.getString("CODE");
        controlfkFlag = mainRS.getString("CONTROLFK_FLAG");
        purposeFulGrantSourceID = mainRS.getString("PURPOSEFULGRANT_FSOURCE_ID");
    }

    private String code="";
    private String controlfkFlag="";

    public String get_controlfk_flag(){
        return controlfkFlag;
    }

    public String get_code(){
        return code;
    }

    public String getCode() {
        return code;
    }

    public String getControlfkFlag() {
        return controlfkFlag;
    }

    public String getPurposeFulGrantSourceID() {
        return purposeFulGrantSourceID;
    }

    public String getPurposeFulGrantSourceName() throws SQLException {
        String sql = "SELECT CAPTION FROM PURPOSEFULGRANT_FSOURCE WHERE ID = " + this.purposeFulGrantSourceID;
        PreparedStatement ps = this.fcon.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            this.purposeFulGrantSourceName = rs.getString("CAPTION");
        }
        return this.purposeFulGrantSourceName;
    }
}
