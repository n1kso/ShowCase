package com.obit.emc.docs.Dictionaries;

import java.sql.SQLException;
import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.docs.additional.PaymentID;
import com.obit.emc.general.emcCustomDic;
import org.w3c.dom.Element;

/**
 * Created with IntelliJ IDEA.
 * User: CherIA
 * Date: 19.08.14
 * Time: 14:07
 * To change this template use File | Settings | File Templates.
 */
public class PayOrder extends emcCustomDic {
    /* Приложение к выписке кредитовое (24) */
    private String docNumber; // Номер документа
    private String docDate; // Дата документа
    private String documentclassId; // Класс документа
    private String recAccount; // Счет получателя

    private PaymentID payment; // Идентификатор платежа

    public PayOrder(Element task, Context con, String id) throws UserException, SQLException {
        super(task, con, id);
        if (id.equals("")) {
            return;
        }
        setMainSQL("", "payorder", "id=?", id);
        build();
    }

    protected void getData() throws UserException, SQLException {
        recAccount = returnNotNull(mainRS.getString("REC_ACCOUNT"));
        documentclassId = returnNotNull(mainRS.getString("DOCUMENTCLASS_ID"));
        docDate = returnNotNull(mainRS.getString("DOC_DATE"));
        docNumber = returnNotNull(mainRS.getString("DOC_NUMBER").trim());

        payment = new PaymentID(
                mainRS.getString("AUTHOR_ID"), mainRS.getString("PI_ANAL_KIND"),
                mainRS.getString("PI_BUDGET_CODE"), mainRS.getString("PAY_OKATO"),
                mainRS.getString("GROUND_ID"), mainRS.getString("TAXPERIOD"),
                mainRS.getString("GRND_DOC_NUMBER"), mainRS.getString("GRND_DOC_DATE"),
                mainRS.getString("PAYTYPE_CODE"));
    }

    public String getDocNumber() {
        return docNumber;
    }

    public String getDocDate() {
        return docDate;
    }

    public String getRecAccount() {
        return recAccount;
    }

    public String getDocumentclassId() {
        return documentclassId;
    }

    public PaymentID getPayment() {
        return payment;
    }

    private String returnNotNull(String value) {
        return value == null ? "" : value;
    }
}
