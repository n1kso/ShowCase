package com.obit.emc.docs;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.docs.Dictionaries.Account;
import com.obit.emc.docs.Dictionaries.Organization;
import com.obit.emc.docs.Dictionaries.PayOrder;
import com.obit.emc.docs.additional.PayDocLines;
import com.obit.emc.general.emcCustomDocument;
import org.w3c.dom.Element;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: CherIA
 * Date: 19.08.14
 * Time: 13:53
 * To change this template use File | Settings | File Templates.
 */
public class MoneyTransferOrder extends emcCustomDocument {

    private final String tableName = "moneytransferorder";

    private String opertypeID; // Тип операции
    private String payAccount; // Счет плательщика
    private String payName; // Название организации
    private String grbsID; // Учредитель
    private BigDecimal amount; // Сумма
    private Organization receiver; // Получатель
    private String recAccount; // л/с получателя
    private String recID; // ID получателя
    private String recAccountID; // ID счета получателя
    private Account receiverAccount; // Счет получателя
    private Account payerAccount; // Счет плательщика
    private String orgAccountID; // ID счета плательщика
    private Organization org; // Плательщик
    private String orgID; // ID плательщика
    private String analKind; // КОСГУ плательщика
    private String _rec_account = "";
    private String _accepted_odl_id = "";
    private String _accepted_odl_id_aubu = "";
    private String _kesr_head_code = "";
    private PayOrder _payOrder = null;

    private ArrayList<PayDocLines> lines = new ArrayList<>(); // Строки документа

    public MoneyTransferOrder(Element task, Context con) throws UserException, SQLException {
        super(task, con);
        setMainSQL("", "moneytransferorder", "id=?", id);
        build();
    }

    @Override
    protected void getData() throws UserException, SQLException {
        if (!mainRS.getString("doc_number").equals("")) {
            opertypeID = mainRS.getString("OPERTYPE_ID");
            payAccount = mainRS.getString("PAY_ACCOUNT");
            payName = mainRS.getString("PAY_NAME");
            orgID = mainRS.getString("PAY_ID");
            orgAccountID = mainRS.getString("PAY_ACC_ID");
            if (orgAccountID != null && orgID != null) {
                org = new Organization(fTask, fcon, orgID, orgAccountID);
            }
            recAccountID = mainRS.getString("REC_ACC_ID");
            recID = mainRS.getString("REC_ID");
            recAccount = mainRS.getString("REC_ACCOUNT");
            receiver = new Organization(fTask, fcon, recID, recAccountID);
            if (orgAccountID != null) {
                payerAccount = new Account(fTask, fcon, orgAccountID);
            }
            if (recAccountID != null) {
                receiverAccount = new Account(fTask, fcon, recAccountID);
            } else
                fcon.throwUserException(new UserException(10806, new String[]{"'Распоряжение на зачисление средств на л/c'",
                        getDocNumber().trim(), getDocDateString()}));
            amount = mainRS.getBigDecimal("AMOUNT");
            grbsID = mainRS.getString("GRBS_ID");

            String linesQuery = "SELECT ind.code, mon.kesr_code, kes.anal_kind, mon.amount, mon.fsr_id, mon.kvr_code  \n" +
                    "    FROM MONEYTRANSFERORDER mon \n" +
                    "    inner JOIN INDUSTRYCODE ind ON ind.ID = mon.INDUSTRYCODE_ID  \n" +
                    "    inner join KES on KES.CODE = mon.KESR_CODE  \n" +
                    "    where document_id= " + getDocumentId() + " and kes.budget_id = mon.budget_id  order by mon.id";
            lines = PayDocLines.getLines(linesQuery, fcon);
            _rec_account = mainRS.getString("rec_account");
            _accepted_odl_id = mainRS.getString("accepted_obl_id");

            String sql = "SELECT documentclass_id, id FROM payorder where document_id=(select parent_id from document where id=" + getDocumentId() + ")";
            ResultSet rs = execSQL(sql);
            if (rs != null) {
                if (rs.getString("documentclass_id").equals("24")) {
                    _payOrder = new PayOrder(fTask, fcon, rs.getString("id"));
                }
            }
            String sql1 = "SELECT documentclass_id, accepted_obl_id, kesr_head_code, cr_anal_kind" +
                    "  FROM AUBUCASHREQUEST where document_id=(select parent_id from document where id=" + getDocumentId() + ")";
            ResultSet rs1 = execSQL(sql1);
            if (rs1 != null) {
                if (rs1.getString("documentclass_id").equals("195")) {
                    _accepted_odl_id_aubu = rs1.getString("accepted_obl_id");
                    _kesr_head_code = rs1.getString("kesr_head_code");
                    analKind = rs1.getString("cr_anal_kind");
                }
            }
        }
    }

    public String GetRecAccount() {
        return _rec_account;
    }

    public PayOrder GetPayOrder() {
        return _payOrder;
    }

    public String GetAcceptOblId() { /*if (_accepted_odl_id == null)_accepted_odl_id="";*/
        return _accepted_odl_id;
    }

    public String GetAcceptOblIdAuBu() {/* if (_accepted_odl_id_aubu == null) _accepted_odl_id_aubu="";*/
        return _accepted_odl_id_aubu;
    }

    public String GetKesrHeadCode() {
        return _kesr_head_code;
    }

    public String getPayAccount() {
        return payAccount;
    }

    public Organization getReceiver() {
        return receiver;
    }

    public String getRecAccount() {
        return recAccount;
    }

    public String getOpertypeID() {
        return opertypeID;
    }

    public Account getReceiverAccount() {
        return receiverAccount;
    }

    public String getRecID() {
        return recID;
    }

    public String getRecAccountID() {
        return recAccountID;
    }

    public ArrayList<PayDocLines> getLines() {
        return lines;
    }

    public String getOrgAccountID() {
        return orgAccountID;
    }

    public Organization getOrg() {
        return org;
    }

    public String getOrgID() {
        return orgID;
    }

    public Account getPayerAccount() {
        return payerAccount;
    }

    public String getPayName() {
        return payName;
    }

    public String getAnalKind() {
        return analKind;
    }

    public String get_accepted_odl_id() {
        return _accepted_odl_id == null ? "" : _accepted_odl_id;
    }

    public String getGrbsID() {
        return grbsID == null ? "" : grbsID;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getTableName() {
        return tableName;
    }
}
