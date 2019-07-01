package com.obit.emc.docs;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.general.emcCustomDocument;
import org.w3c.dom.Element;

import java.sql.SQLException;

/**
 * Created by ChesDV on 06.07.2017.
 */

public class Vcd extends emcCustomDocument {
    private final String tableName = "ASSETACT";

    public Vcd(Element task, Context con) throws UserException, SQLException {
        super(task, con);
        setMainSQL("select * from ASSETACT where id = " + id);
        build();

    }

    @Override
    protected void getData() throws UserException, SQLException {
        pay_acc = mainRS.getString("Pay_Account");
        oper_type = mainRS.getString("Opertype_Id");
        kind_class = mainRS.getString("Pi_Anal_Kind");
        rec_acc = mainRS.getString("Rec_Account");
    }

    private String pay_acc = "";
    private String oper_type = "";
    private String kind_class = "";
    private String rec_acc = "";


    public String Getpay_acc() {
        return pay_acc;
    }

    public String Getoper_type() {
        return oper_type;
    }

    public String Getkind_class() {
        return kind_class;
    }

    public String Getrec_acc() {
        return rec_acc;
    }

    public String getTableName() {
        return tableName;
    }
}
