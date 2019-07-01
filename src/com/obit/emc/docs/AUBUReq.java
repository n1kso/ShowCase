package com.obit.emc.docs;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.docs.Dictionaries.Account;
import com.obit.emc.docs.Dictionaries.Bank;
import com.obit.emc.docs.Dictionaries.Contract;
import com.obit.emc.docs.Dictionaries.Organization;
import com.obit.emc.docs.additional.PayDocLines;
import com.obit.emc.docs.additional.PaymentID;
import com.obit.emc.general.emcCustomDocument;
import org.w3c.dom.Element;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

// Заявка БУ/АУ на выплату средств
public class AUBUReq extends emcCustomDocument {

    private final String tableName = "AUBUCASHREQUEST";

    private String paykind; // Вид платежа
    private String description; // Назначение платежа
    private String codeKESR; // КОСГУ
    private String opertypeID; // Тип операции
    private byte woSpending; // Без права расходования

    // Плательщик
    private Organization org; // Плательщик
    private String payID; // ID плательщика
    private String orgAccountID; // ID счета плательщика
    private String payAccount; // Номер счета плательщика
    private String payUFKAccount; // Счет УФК плательщика
    private Account payerAccount; // Счет плательщика

    // Получатель
    private Organization receiver; // Получатель
    private String recKpp; // КПП получателя
    private String recID; // ID получателя
    private String recName; // Краткое наименование получателя из ЭД
    private String recAccountID; // ID счета получателя
    private String recBIK; // БИК банка получателя
    private String recAccountNumber; // Номер счета получателя
    private String recUFKAccount; // Счет УФК получателя
    private Account recAccount; // Счет получателя
    private Bank bankPayee; // Банк получателя

    //Учредитель
    private String orgID; // ID учредителя

    // Классификация получателя
    private String industryCode; // Отраслевой код из классификации получателя
    private String KVRHeadCode; // КВР
    private String KSDAHeadCode; // Ан.группа
    private String KFSRHeadCode; // КФСР
    private String kesrHeadCode; // КОСГУ из классификации получателя
    private String kesrAnalKind; // Бюджетный классификатор КОСГУ
    private String grandinvestmentHeadID; // ID кода субсидии
    private String fsrHeadID; // КВФО из классификации получателя

    private BigDecimal amount; // общая сумма
    private BigDecimal _amount;
    private BigDecimal lastYearAmt;
    private BigDecimal rsvAmount;
    private BigDecimal contractAmount;
    private BigDecimal fullAmount;

    private String accepted_obl_id = "";
    private Contract contract = null;
    private Context fcon;
    private Element ftask;

    private ArrayList<PayDocLines> lines = new ArrayList<>(); // Строки заявки
    private PaymentID identPlat; // Идентификатор платежа

    public AUBUReq(Element task, Context con) throws UserException, SQLException {
        super(task, con);
        fcon = con;
        ftask = task;
        setMainSQL("SELECT * FROM AUBUCASHREQUEST INNER JOIN INDUSTRYCODE ON INDUSTRYCODE.ID = AUBUCASHREQUEST.INDUSTRYCODE_ID where document_id=(select document_id from AUBUCASHREQUEST where id=" + id + ")");
        build();
    }

    @Override
    protected void getData() throws UserException, SQLException {
        if (!mainRS.getString("DOC_NUMBER").equals("")) {
            paykind = mainRS.getString("PAYKIND");
            description = mainRS.getString("DESCRIPTION");
            codeKESR = mainRS.getString("KESR_CODE");
            opertypeID = mainRS.getString("OPERTYPE_ID");
            amount = mainRS.getBigDecimal("AMOUNT");
            woSpending = mainRS.getByte("WO_SPENDING");

            payID = mainRS.getString("PAY_ID");
            orgAccountID = mainRS.getString("PAY_ACC_ID");
            payAccount = mainRS.getString("pay_account");
            payUFKAccount = mainRS.getString("PAY_UFK_ACCOUNT");
            payerAccount = new Account(ftask, fcon, orgAccountID);
            org = new Organization(fTask, fcon, payID, orgAccountID);

            orgID = mainRS.getString("ORG_ID");

            recKpp = mainRS.getString("REC_KPP");
            recID = mainRS.getString("REC_ID");
            recName = mainRS.getString("REC_NAME");
            recAccountID = mainRS.getString("REC_ACC_ID");
            recBIK = mainRS.getString("REC_BIC");
            recAccountNumber = mainRS.getString("REC_ACCOUNT");
            recUFKAccount = mainRS.getString("REC_UFK_ACCOUNT");

            if (recAccountID != null) {
                recAccount = new Account(ftask, fcon, recAccountID);
            } else fcon.throwUserException(new UserException(10806, new String[]{"'Заявка БУ/АУ на выплату средств'",
                    getDocNumber().trim(), getDocDateString()}));

            bankPayee = new Bank(fTask, fcon, mainRS.getString("REC_BIC"), "");
            receiver = new Organization(fTask, fcon, recID, recAccountID);

            // Классификация получателя
            String recieverClassificationQuery = "SELECT INDUSTRYCODE.code as code, KES.anal_kind as anal_kind  FROM AUBUCASHREQUEST " +
                    "left JOIN INDUSTRYCODE ON INDUSTRYCODE.ID = AUBUCASHREQUEST.INDUSTRYCODE_HEAD_ID " +
                    "left join KES on KES.CODE = AUBUCASHREQUEST.KESR_HEAD_CODE " +
                    "where document_id=" + getDocumentId();

            ResultSet RS = execSQL(recieverClassificationQuery);
            if (RS != null) {
                industryCode = RS.getString("CODE");
                kesrAnalKind = RS.getString("anal_kind");
            }
            KVRHeadCode = mainRS.getString("KVR_HEAD_CODE");
            KSDAHeadCode = mainRS.getString("KSDA_HEAD_CODE");
            KFSRHeadCode = mainRS.getString("KFSR_HEAD_CODE");
            kesrHeadCode = mainRS.getString("KESR_HEAD_CODE");
            grandinvestmentHeadID = mainRS.getString("GRANTINVESTMENT_HEAD_ID");
            fsrHeadID = mainRS.getString("FSR_HEAD_ID");

            String linesQuery = "SELECT INDUSTRYCODE.code as code, AUBUCASHREQUEST.kesr_code as kesr_code, KES.anal_kind as anal_kind," +
                    " AUBUCASHREQUEST.amount as amount, AUBUCASHREQUEST.KVR_CODE, AUBUCASHREQUEST.fsr_id as fsr_id" +
                    "  FROM AUBUCASHREQUEST " +
                    "inner JOIN INDUSTRYCODE ON INDUSTRYCODE.ID = AUBUCASHREQUEST.INDUSTRYCODE_ID " +
                    "inner join KES on KES.CODE = AUBUCASHREQUEST.KESR_CODE " +
                    "where document_id=" + getDocumentId() + " AND KESR_CODE IS NOT NULL and KES.budget_id = aubucashrequest.budget_id order by AUBUCASHREQUEST.id ";

            //Строки документа
            lines = PayDocLines.getLines(linesQuery, fcon);

            // Идентификатор платежа
            identPlat = new PaymentID(
                    mainRS.getString("AUTHOR_ID"), mainRS.getString("PI_ANAL_KIND"),
                    mainRS.getString("PI_BUDGET_CODE"), mainRS.getString("PAY_OKATO"),
                    mainRS.getString("GROUND_ID"), mainRS.getString("TAXPERIOD"),
                    mainRS.getString("GRND_DOC_NUMBER"), mainRS.getString("GRND_DOC_DATE"), mainRS.getString("PAYTYPE_CODE"));
            _amount = mainRS.getBigDecimal("amount");
            accepted_obl_id = mainRS.getString("ACCEPTED_OBL_ID");
        }

        if (getAcceptedOblId() != null && !getAcceptedOblId().equals("")) {
            this.contract = new Contract(fTask, fcon, accepted_obl_id);
            lastYearAmt = contract.getLastYearAmt();
            rsvAmount = contract.getRsvAmount();
            contractAmount = contract.getContractAmount();
            fullAmount = contract.getFullAmount();
        }
    }

    public String getAcceptedOblId() {
        if (accepted_obl_id == null) return "";
        return accepted_obl_id.trim();
    }

    public String getPaykind() {
        if (paykind == null) return "";
        return paykind;
    }

    public String getDescription() {
        if (description == null) return "";
        return description;
    }

    public String getCodeKESR() {
        return codeKESR;
    }

    public String getOpertypeID() {
        return opertypeID;
    }

    public Organization getOrg() {
        return org;
    }

    public String getPayID() {
        return payID;
    }

    public String getOrgAccountID() {
        return orgAccountID;
    }

    public Account getPayerAccount() {
        return payerAccount;
    }

    public String getPayAccount() {
        if (payAccount == null) return "";
        return payAccount;
    }

    public String getPayUFKAccount() {
        if (payUFKAccount == null) return "";
        return payUFKAccount;
    }

    public Organization getReceiver() {
        return receiver;
    }

    public String getRecKpp() {
        return recKpp;
    }

    public String getRecID() {
        return recID;
    }

    public String getRecName() {
        return recName;
    }

    public String getRecAccountID() {
        return recAccountID == null ? "": recAccountID;
    }

    public String getRecBIK() {
        return recBIK;
    }

    public String getRecAccountNumber() {
        return recAccountNumber;
    }

    public Account getRecAccount() {
        return recAccount;
    }

    public String getRecUFKAccount() {
        if (recUFKAccount == null) return "";
        return recUFKAccount;
    }

    public Bank getBankPayee() {
        return bankPayee;
    }

    public String getIndustryCode() {
        if (industryCode == null) return "";
        return industryCode;
    }

    public String getKesrAnalKind() {
        return kesrAnalKind == null ? "" : kesrAnalKind;
    }

    public String getKVRHeadCode() {
        if (KVRHeadCode == null) return "";
        return KVRHeadCode;
    }

    public String getKSDAHeadCode() {
        if (KSDAHeadCode == null) return "";
        return KSDAHeadCode;
    }

    public String getKFSRHeadCode() {
        if (KFSRHeadCode == null) return "";
        return KFSRHeadCode;
    }

    public String getKesrHeadCode() {
        if (kesrHeadCode == null) return "";
        return kesrHeadCode;
    }

    public String getGrandinvestmentHeadID() {
        if (grandinvestmentHeadID == null) return "";
        return grandinvestmentHeadID;
    }

    public String getFsrHeadID() {
        if (fsrHeadID == null) return "";
        return fsrHeadID;
    }

    public String getOrgID() {
        return orgID;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal get_amount() {
        return _amount;
    }

    public BigDecimal getLastYearAmt() {
        return lastYearAmt;
    }

    public BigDecimal getRsvAmount() {
        return rsvAmount;
    }

    public BigDecimal getContractAmount() {
        return contractAmount;
    }

    public BigDecimal getFullAmount() {
        return fullAmount;
    }

    public ArrayList<PayDocLines> getLines() {
        return lines;
    }

    public PaymentID getIdentPlatTest() {
        return identPlat;
    }

    public String getTableName() {
        return tableName;
    }

    public Contract getContract() {
        return contract;
    }

    public byte getWoSpending() {
        return woSpending;
    }
}
