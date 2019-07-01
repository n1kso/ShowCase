package com.obit.emc.docs;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.docs.Dictionaries.Bank;
import com.obit.emc.general.emcCustomDocument;
import org.w3c.dom.Element;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: OvsAN
 * Date: 27.01.15
 * Time: 10:56
 * To change this template use File | Settings | File Templates.
 */
//Платежное поручение
public class Pp extends emcCustomDocument {

    private final String tableName = "PAYORDER";

    public Pp(Element task, Context con) throws UserException, SQLException {
        super(task, con);
        setMainSQL("SELECT * FROM PAYORDER WHERE id=" + id);
        build();
    }

    @Override
    protected void getData() throws UserException, SQLException {
        description = mainRS.getString("DESCRIPTION");
        _bankPayee = new Bank(fTask, fcon, mainRS.getString("REC_BIC"), "");
    }



    /*Назначение платежа*/
    private String description = "";
    /*Банк получателя*/
    private Bank _bankPayee;

    public Bank getBankPayee() {
        return _bankPayee;
    }

    public String getDescription() {
        return description;
    }

    public String getTableName() {
        return tableName;
    }
}
