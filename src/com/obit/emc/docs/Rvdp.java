package com.obit.emc.docs;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.docs.Dictionaries.Account;
import com.obit.emc.general.emcCustomDocument;
import org.w3c.dom.Element;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: MakKN
 * Date: 12.11.14
 * Time: 11:25
 * To change this template use File | Settings | File Templates.
 */
//Распоряжение на выплату по договору привлечения средств
//Уведомление о поступлении средств по договору привлечения средств
public class Rvdp extends emcCustomDocument {

    private final String tableName = "LN_PAYDOC";

    public Rvdp(Element task, Context con) throws UserException, SQLException {

        super(task, con);
        setMainSQL(" select * from LN_PAYDOC where id=" + id);
        build();
    }

    @Override
    protected void getData() throws UserException, SQLException {
        _orgaccountID = mainRS.getString("ADMIN_ORG_ACCOUNT_ID");
        _payTypeID = mainRS.getString("PAYTYPE_ID");
        if (!_orgaccountID.equals("")) {
            _orgAccount = new Account(fTask, fcon, _orgaccountID);
        }
    }

    private Account _orgAccount = null;
    private String _payTypeID = "";
    private String _orgaccountID;

    public Account getOrgAccount() {
        return _orgAccount;
    }

    public String get_payTypeID() {
        return _payTypeID;
    }

    public String getTableName() {
        return tableName;
    }
}
