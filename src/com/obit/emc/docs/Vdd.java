package com.obit.emc.docs;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.general.emcCustomDocument;
import org.w3c.dom.Element;

import java.sql.SQLException;

/**
 * Created by makkn on 03.07.2017.
 */
public class Vdd extends emcCustomDocument {
    private final String tableName = "ASSETACT";

    public Vdd(Element task, Context con) throws UserException, SQLException {
        super(task, con);
        setMainSQL("select * from ASSETACT where id = " + id);
        build();
    }

    @Override
    protected void getData() throws UserException, SQLException {
        pay_account = mainRS.getString("Pay_Account");
        oper_type = mainRS.getString("Opertype_Id");
    }

    private String pay_account = "";
    private String oper_type = "";

    public String Getpay_account() {
        return pay_account;
    }

    public String Getoper_type() {
        return oper_type;
    }

    public String getTableName() {
        return tableName;
    }
}
