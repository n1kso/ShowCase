package com.obit.emc.docs;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.docs.Dictionaries.*;
import com.obit.emc.docs.additional.IdPlat;
import com.obit.emc.docs.additional.Kbk;
import com.obit.emc.docs.additional.PaymentID;
import com.obit.emc.func.func;
import com.obit.emc.general.emcCustomDocument;
import org.w3c.dom.Element;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Zor extends emcCustomDocument {

    private Context fcon;
    private Element ftask;
    private String sql;
    private ResultSet rs;

    private String paykind; // Вид платежа
    private Estimate est; // Бланк расходов
    private BigDecimal amount; // Сумма
    private String description; // Назначение платежа
    private String codeKESR; // КОСГУ
    private Respperson respperson; // Ответственные лица
    private String BOid; // ID бюджетного обязательства
    private BudgetObligation bo; // Бюджетное обязательство
    // Плательщик
    private Organization org; // Плательщик
    private String orgID; // ID плательщика
    private String orgaccountID; // ID счета для финансирования
    private String orgAccountNumber; // Номер счета для финансирования
    private Account orgAccount; // Счет плтельщика
    private String payUFKAccountNumber; // Счет УФК плательщика
    // Получатель
    private Organization receiver; // Получатель
    private String recID; // ID получателя
    private String recName; // Краткое наименование получателя из ЭД
    private String recBIC; // БИК банка получателя в ЭД
    private String recBIKdb; // БИК банка из справочника банков
    private String acc_type;
    private String recKpp; // КПП получателя
    private String recAccountNumber; // Номер счета получателя
    private String recAccountID; // ID счета получателя
    private String recCorAccount; // Коррсчет получателя
    private String recUFKAccountID; // ID счета УФК
    private String recUFKAccountNumber; //Счет УФК
    private Account recAccount; // Счет получателя
    private Bank bankPayee; // Банк получателя

    private IdPlat identPlat; // Идентификатор платежа
    private PaymentID identPlatTest;
    private Kbk kbk; // КБК
    private String codePurpose; // ID кода цели

    private String idContract; // ID контракта
    private Boolean amtContract = false; // Сумма контракта мньше исполнения
    private Contract contract;

    private BigDecimal contractAmount;
    private BigDecimal lastYearAmt;
    private BigDecimal fullAmount;
    private BigDecimal rsvAmount;
    private BigDecimal contractAmountIspolnenie;

    private final String tableName = "BUDGORDER";


    public Zor(Element task, Context con) throws UserException, SQLException {
        super(task, con);
        fcon = con;
        ftask = task;
        setMainSQL("", "budgorder", "id=?", id);
        build();
    }

    @Override
    protected void getData() throws UserException, SQLException {


        paykind = mainRS.getString("PAYKIND");

        est = new Estimate(fTask, fcon, mainRS.getString("ESTIMATE_ID"));
        try {
            amount = mainRS.getBigDecimal("amount");
        } catch (NullPointerException e) {
            e.getStackTrace();
        }

        description = mainRS.getString("DESCRIPTION");
        codeKESR = mainRS.getString("KESR_CODE");
        orgID = mainRS.getString("Recipient_ID");
        orgaccountID = mainRS.getString("ORGACCOUNT_ID");
        orgAccountNumber = mainRS.getString("ORG_ACCOUNT");
        orgAccount = new Account(ftask, fcon, orgaccountID);
        recName = mainRS.getString("REC_NAME");
        recID = mainRS.getString("REC_ID");
        recBIC = mainRS.getString("REC_BIC");
        recKpp = mainRS.getString("REC_KPP");
        recAccountNumber = mainRS.getString("REC_ACCOUNT");
        recAccountID = mainRS.getString("REC_ACC_ID");
        recCorAccount = mainRS.getString("REC_CORACCOUNT");
        recUFKAccountID = mainRS.getString("REC_UFK_ACC_ID");
        recUFKAccountNumber = mainRS.getString("REC_UFK_ACCOUNT");
        payUFKAccountNumber = mainRS.getString("ORG_UFK_ACCOUNT");

        try {
            recAccount = new Account(fTask, fcon, recAccountID);
        } catch (Exception e) {
            fcon.throwUserException(new UserException(10806, new String[]{"Заявка на оплату расходов", getDocNumber().trim(), getDocDateString()}));
        }

        // данные плательщика и получателя в справочнике
        org = new Organization(fTask, fcon, getOrgID(), getOrgaccountID());
        if (getRecID() == null) fcon.throwUserException(new UserException("Отсутствует ссылка на получателя."), false);
        else receiver = new Organization(fTask, fcon, getRecID(), getRecAccountID());

        // Идентификатор платежа
        identPlat = new IdPlat(
                mainRS.getString("AUTHOR_ID"), mainRS.getString("PI_ANAL_KIND"),
                mainRS.getString("PI_BUDGET_CODE"), mainRS.getString("PAY_OKATO"),
                mainRS.getString("GROUND_ID"), mainRS.getString("TAXPERIOD"),
                mainRS.getString("GRND_DOC_NUMBER"), mainRS.getString("GRND_DOC_DATE"), fcon);

        identPlatTest = new PaymentID(
                mainRS.getString("AUTHOR_ID"), mainRS.getString("PI_ANAL_KIND"),
                mainRS.getString("PI_BUDGET_CODE"), mainRS.getString("PAY_OKATO"),
                mainRS.getString("GROUND_ID"), mainRS.getString("TAXPERIOD"),
                mainRS.getString("GRND_DOC_NUMBER"), mainRS.getString("GRND_DOC_DATE"), mainRS.getString("PAYTYPE_CODE"));

        respperson = new Respperson(ftask, fcon, orgID); // Ответственные лица
        BOid = mainRS.getString("BO_ID");

        // Коды бюджетной классификации
        kbk = new Kbk(mainRS.getString("KFSR_CODE"), mainRS.getString("KCSR_CODE"),
                mainRS.getString("KVR_CODE"), mainRS.getString("KESR_CODE"),
                mainRS.getString("KADMR_CODE"), mainRS.getString("KDF_CODE"),
                mainRS.getString("KDE_CODE"), mainRS.getString("KDR_CODE"),
                mainRS.getString("FSR_ID"));

        codePurpose = mainRS.getString("PURPOSEFULGRANT_ID");

        if (recAccountID != null) {
            if (!checkBIKUFK(recAccountID)) checkBIKUFK(recUFKAccountID);
        }

        bankPayee = new Bank(fTask, fcon, mainRS.getString("REC_BIC"), "");

        //бюджетное облязательство
        if (getBO_id() != null && !getBO_id().equals("")) {
            bo = new BudgetObligation(fTask, fcon, BOid);
            Document doc_parent = new Document(fTask, fcon, bo.getDocumentId());
            sql = "select id from contract where document_id=" + doc_parent.getParentID();
            rs = execSQL(sql);
            if (rs != null) {
                idContract = rs.getString("ID");
            }
            contract = new Contract(fTask, fcon, idContract);
            contractAmount = contract.getContractAmount();
            lastYearAmt = contract.getLastYearAmt();
            fullAmount = contract.getFullAmount();
            rsvAmount = contract.getRsvAmount();
        }
    }


    public String getPaykind() {
        if (paykind == null) return "";
        return paykind;
    }

    public String getBIC() {
        return recBIC;
    }

    public String getRecID() {
        return recID;
    }

    public String getRecAccountNumber() {
        if (recAccountNumber == null) return "";
        return recAccountNumber;
    }

    public String getRecCorAccount() {
        if (recCorAccount == null) return "";
        return recCorAccount;
    }

    public String getRecAccountID() {
        return recAccountID == null ? "" : recAccountID;
    }

    public String getRecKpp() {
        return recKpp;
    }

    public String getOrgID() {
        return orgID;
    }

    public String getOrgaccountID() {
        return orgaccountID;
    }

    public String getRecName() {
        return recName;
    }

    public Organization getOrg() {
        return org;
    }

    public Organization getReceiver() {
        return receiver;
    }

    public IdPlat getIdentPlat() {
        return identPlat;
    }

    public PaymentID getIdentPlatTest() {
        return identPlatTest;
    }

    public String getDescription() {
        if (description == null) description = "";
        return description;
    }

    public String getCodeKESR() {
        return codeKESR;
    }

    public Kbk getKBK() {
        return kbk;
    }

    public Estimate getEst() {
        return est;
    }

    public Respperson getRespperson() {
        return respperson;
    }

    public String getBO_id() {
        if (BOid == null) return "";
        return BOid.trim();
    }

    public Account getRecAccount() {
        return recAccount;
    }

    public Account getOrgAccount() {
        return orgAccount;
    }

    public String getBIKdb() {
        if (recBIKdb == null) recBIKdb = "";
        return recBIKdb;
    }

    public Bank getBankPayee() {
        return bankPayee;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String get_Acc_type() {
        if (acc_type == null) return "";
        return acc_type;
    }

    public BudgetObligation getBo() {
        return bo;
    }

    public Boolean getAmtContract() {
        return amtContract;
    }

    public String getRecUFKAccountNumber() {
        if (recUFKAccountNumber == null) return "";
        return recUFKAccountNumber;
    }

    public String getPayUFKAccountNumber() {
        if (payUFKAccountNumber == null) return "";
        return payUFKAccountNumber;
    }

    public BigDecimal getContractAmount() {
        return contractAmount;
    }

    public BigDecimal getLastYearAmt() {
        return lastYearAmt;
    }

    public BigDecimal getFullAmount() {
        return fullAmount;
    }

    public BigDecimal getRsvAmount() {
        return rsvAmount;
    }

    public String getContractAmountIspolnenie() {
        return contractAmountIspolnenie.toString();
    }

    public String getCodePurpose() {
        return codePurpose;
    }

    public String getTableName() {
        return tableName;
    }

    public Contract getContract() {
        return contract;
    }

    /**
     * Для заявок с л/с брать БИК счета УФК
     *
     * @param id счета (р/с, УФК)
     * @return true если значения будут получены из БД
     * @throw SQLException
     */
    private Boolean checkBIKUFK(String id) throws SQLException {
        sql = "select bank.bic,orgaccount.orgacctype_id " +
                "from bank inner join orgaccount on orgaccount.bank_id=bank.id " +
                "where bank.id = (select bank_id from orgaccount where id ='" + id + "')";
        rs = execSQL(sql);
        if (rs != null) {
            recBIKdb = rs.getString("BIC");
            acc_type = rs.getString("orgacctype_id");
            return true;
        }
        return false;
    }
}
