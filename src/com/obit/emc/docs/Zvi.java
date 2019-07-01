package com.obit.emc.docs;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.general.emcCustomDocument;
import org.w3c.dom.Element;

import java.sql.SQLException;

/**
 * Created by ChesDV on 07.07.2017.
 */
public class Zvi extends emcCustomDocument {

    private final String tableName = "SRCORDER";

    public Zvi(Element task, Context con) throws UserException, SQLException {
        super(task, con);
        setMainSQL("select * from SRCORDER where id = " + id);
        build();
    }

    @Override
    protected void getData() throws UserException, SQLException {
        oper_type = mainRS.getString("Opertype_Id");
        kvi = mainRS.getString("Ki_Code");
        pay_acc = mainRS.getString("Rec_Account");
    }

    private String oper_type = "";
    private String kvi = "";
    private String pay_acc = "";

    public String Getoper_type() {
        return oper_type;
    }

    public String Getkvi() {
        return kvi;
    }

    public String Getpay_acc() {
        return pay_acc;
    }

    public String getTableName() {
        return tableName;
    }
}