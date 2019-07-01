package com.obit.emc.docs;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.docs.Dictionaries.Account;
import com.obit.emc.general.emcCustomDocument;
import org.w3c.dom.Element;

import java.sql.SQLException;

// Распоряжение на зачисление специальных средств - 74
public class Rzss extends emcCustomDocument {

    private final String tableName = "PAYINCORDER";

    private String docNumber; // Номер документа
    private String docDate; // Дата документа
    private String operTypeID; // Тип операции
    private String orgaccountID; // ID счета
    private Account orgAccount; // Счет

    public Rzss(Element task, Context con) throws UserException, SQLException {

        super(task, con);
        setMainSQL(" select * from PAYINCORDER where id=" + id);
        build();
    }

    @Override
    protected void getData() throws UserException, SQLException {
        operTypeID = mainRS.getString("OPERTYPE_ID");

        orgaccountID = mainRS.getString("ORGACCOUNT_ID");
        if (!orgaccountID.equals("")) {
            orgAccount = new Account(fTask, fcon, orgaccountID);
        }
    }

    public String getOperTypeID() {
        return operTypeID;
    }
    public Account getOrgAccount() {
        return orgAccount;
    }
    public String getTableName() {
        return tableName;
    }
}
