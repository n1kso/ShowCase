package com.obit.emc.docs;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.docs.Dictionaries.Account;
import com.obit.emc.general.emcCustomDocument;
import org.w3c.dom.Element;

import java.sql.SQLException;

// ������������ �� ���������� ����������� ������� - 74
public class Rzss extends emcCustomDocument {

    private final String tableName = "PAYINCORDER";

    private String docNumber; // ����� ���������
    private String docDate; // ���� ���������
    private String operTypeID; // ��� ��������
    private String orgaccountID; // ID �����
    private Account orgAccount; // ����

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
