package com.obit.emc.docs;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.docs.Dictionaries.Account;
import com.obit.emc.docs.Dictionaries.Bank;
import com.obit.emc.docs.Dictionaries.Organization;
import com.obit.emc.docs.additional.IdPlat;
import com.obit.emc.docs.additional.PaymentID;
import com.obit.emc.general.emcCustomDocument;
import org.w3c.dom.Element;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: CherIA
 * Date: 24.06.13
 * Time: 16:23
 * To change this template use File | Settings | File Templates.
 */
public class Znsss extends emcCustomDocument {

    private final String tableName = "PAYREQUEST";

    // Плательщик
    private String orgID; // ID организации
    private String orgAccountID; // ID счет организации
    private Organization org;

    // Получатель
    private String recID; // ID организации получателя
    private String recAccountID; // ID счета получателя
    private Organization receiver;
    private String recUFKAccountNumber; // Счет УФК получателя
    private String recAccountNumber; // Номер счета получателя
    private PaymentID identPlatTest;
    private String description;
    private Bank _bankPayee;

    private String _operTypeID = "";
    private Account _orgAccount = null;
    private String _orgaccountID = "";
    private String paykind = "";
    private String _rec_account = "";
    private IdPlat identPlat = null;
    private String IdenPlat = "";
    private String OktmoIdPl = "";
    private Context fcon;
    private Element ftask;
    private String payID = "";

    public Znsss(Element task, Context con) throws UserException, SQLException {

        super(task, con);
        fcon = con;
        ftask = task;
        setMainSQL(" select * from PAYREQUEST where id=" + id);
        build();
    }

    @Override
    protected void getData() throws UserException, SQLException {

        orgID = mainRS.getString("RECIPIENT_ID");
        orgAccountID = mainRS.getString("PAY_ACC_ID");
        org = new Organization(ftask, fcon, orgID, orgAccountID);

        recID = mainRS.getString("REC_ID");
        recAccountID = mainRS.getString("REC_ACC_ID");
        receiver = new Organization(ftask, fcon, recID, recAccountID);

        recAccountNumber = mainRS.getString("REC_ACCOUNT");
        recUFKAccountNumber = mainRS.getString("REC_UFK_ACCOUNT");

        identPlatTest = new PaymentID(
                mainRS.getString("AUTHOR_ID"), mainRS.getString("PI_ANAL_KIND"),
                mainRS.getString("PI_BUDGET_CODE"), mainRS.getString("PAY_OKATO"),
                mainRS.getString("GROUND_ID"), mainRS.getString("TAXPERIOD"),
                mainRS.getString("GRND_DOC_NUMBER"), mainRS.getString("GRND_DOC_DATE"),
                mainRS.getString("PAYTYPE_CODE"));

        paykind = mainRS.getString("PAYKIND");
        _orgaccountID = mainRS.getString("PAY_ACC_ID");
        _operTypeID = mainRS.getString("OPERTYPE_ID");

        IdenPlat = mainRS.getString("AUTHOR_ID");
        OktmoIdPl = mainRS.getString("PAY_OKATO");
        description = mainRS.getString("DESCRIPTION");
        payID = mainRS.getString(("PAY_ID"));

        _bankPayee = new Bank(fTask, fcon, mainRS.getString("REC_BIC"), "");

        if (!_orgaccountID.equals("")) {
            _orgAccount = new Account(fTask, fcon, _orgaccountID);
        }

        // Идентификатор платежа
        identPlat = new IdPlat(
                mainRS.getString("AUTHOR_ID"), mainRS.getString("PI_ANAL_KIND"),
                mainRS.getString("PI_BUDGET_CODE"), mainRS.getString("PAY_OKATO"),
                mainRS.getString("GROUND_ID"), mainRS.getString("TAXPERIOD"),
                mainRS.getString("GRND_DOC_NUMBER"), mainRS.getString("GRND_DOC_DATE"), fcon);
    }

    public Account getOrgAccount() {
        return _orgAccount;
    }

    public String get_operTypeID() {
        return _operTypeID;
    }

    public String getPaykind() {
        if (paykind == null) return "";
        return paykind;
    }

    public String GetRecAccount() {
        _rec_account = recAccountNumber;
        return _rec_account;
    }

    public String getRecUFKAccountNumber() {
        return recUFKAccountNumber;
    }

    public IdPlat getIdentPlat() {
        return identPlat;
    }

    public String getIdenPlat() {
        if (IdenPlat == null) {
            IdenPlat = "";
            return IdenPlat;
        } else if (IdenPlat.equals("")) {
            IdenPlat = "";
            return IdenPlat;
        } else
            return IdenPlat;
    }

    public String getOktmoIdPl() {
        if (OktmoIdPl == null) {
            OktmoIdPl = "";
            return OktmoIdPl;
        } else return OktmoIdPl;
    }

    public String getRecAccountID() {
        return recAccountID == null ? "" : recAccountID;
    }
    public String getDescription() {
        if (description == null) description = "";
        return description;
    }
    public Bank getBankPayee() {
        return _bankPayee;
    }
    public String getPayID() {
        return payID;
    }
    public Organization getOrg() {
        return org;
    }
    public Organization getReceiver() {
        return receiver;
    }
    public String getRecAccountNumber() {
        return recAccountNumber;
    }
    public PaymentID getIdentPlatTest() {
        return identPlatTest;
    }
    public String getTableName() {
        return tableName;
    }
}
