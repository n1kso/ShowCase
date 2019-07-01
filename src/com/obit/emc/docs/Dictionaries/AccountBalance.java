package com.obit.emc.docs.Dictionaries;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.general.emcCustomDic;
import org.w3c.dom.Element;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: KanMA
 * Date: 31.01.13
 * Time: 11:22
 * To change this template use File | Settings | File Templates.
 */
public class AccountBalance extends emcCustomDic {
    /* ‘инќрган счета организации*/

    private String accountNumber; // —чет организации

    public AccountBalance(Element task, Context con, String id) throws UserException, SQLException {
        super(task,con,"");
        setMainSQL("select account_number ACCNUMBER from orgaccount where id=" + id);
        build();
    }

    protected void getData() throws UserException, SQLException {
        accountNumber = mainRS.getString("ACCNUMBER");
    }

    public String getAccNumber() {
        return accountNumber;
    }
}
