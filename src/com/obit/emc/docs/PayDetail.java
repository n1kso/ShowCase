package com.obit.emc.docs;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.docs.Dictionaries.Organization;
import com.obit.emc.docs.additional.PayDetailLine;
import com.obit.emc.general.emcCustomDocument;
import org.w3c.dom.Element;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

// Cправка-уведомление об уточнении операций АУ/БУ
public class PayDetail extends emcCustomDocument {

    private final String tableName = "paydetail";

    private Context fcon;
    private Element ftask;
    private String operTypeID; // Тип операции
    private Organization org; // Учреждение
    private Organization payOrg; // Учреждение в уточненной строке
    private Organization recOrg; // Учреждение в уточненной строке
    private String orgAccountID; // ID л/с уточненной строки
    private BigDecimal amount; // Сумма
    private String orgGrbsID; // учредитель
    private String payID; // ID организации в уточняемой


    private ArrayList<PayDetailLine> sourceLines = new ArrayList<>(); // Уточняемые строки
    private ArrayList<PayDetailLine> dstLines = new ArrayList<>(); // Уточненные строки


    public PayDetail(Element task, Context con) throws UserException, SQLException {
        super(task, con);
        ftask = task;
        fcon = con;
        setMainSQL("", "paydetail", "id=?", id);
        build();
    }

    @Override
    protected void getData() throws UserException, SQLException {
        if (!mainRS.getString("doc_number").equals("")) {
            operTypeID = mainRS.getString("OPERTYPE_ID");
            payID = mainRS.getString(("ORG_ID"));
            orgAccountID = mainRS.getString("ORG_ACC_ID");
            org = new Organization(fTask, fcon, payID, orgAccountID);
            amount = mainRS.getBigDecimal("AMOUNT");
            orgGrbsID = mainRS.getString("ORG_GRBS_ID");
        }
        sourceLines = PayDetailLine.getPayDetailLine(getSQL(id, "1"), fcon, ftask);
        dstLines = PayDetailLine.getPayDetailLine(getSQL(id, "0"), fcon, ftask);

        payOrg = new Organization(fTask, fcon, getSourceLines().get(0).getOrgId(), getSourceLines().get(0).getAccId());
        recOrg = new Organization(fTask, fcon, getDstLines().get(0).getOrgId(), getDstLines().get(0).getAccId());
    }

    private static String getSQL(String id, String sourceLine) {
        return "SELECT * FROM PAYDETAILLINE p " +
                "inner JOIN INDUSTRYCODE ON INDUSTRYCODE.ID = p.INDUSTRYCODE_ID " +
                "where paydetail_id=" + id + " AND p.IS_SOURCE_LINE=" + sourceLine + " order by p.LINE_NUM";
    }

    public String getOperTypeID() {
        return operTypeID;
    }

    public String getPayID() {
        return payID;
    }

    public ArrayList<PayDetailLine> getSourceLines() {
        return sourceLines;
    }

    public ArrayList<PayDetailLine> getDstLines() {
        return dstLines;
    }

    public Organization getOrg() {
        return org;
    }

    public String getOrgAccountID() {
        return orgAccountID;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getOrgGrbsID() {
        return orgGrbsID;
    }

    public Organization getRecOrg() {
        return recOrg;
    }

    public Organization getPayOrg() {
        return payOrg;
    }

    public String getTableName() {
        return tableName;
    }
}
