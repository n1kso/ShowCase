package com.obit.emc.docs.additional;

import com.bssys.server.Context;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

// Строки Заявки БУ/АУ на выплату средств
public class PayDocLines {

    private String kesrCode; // КОСГУ
    private String kesrAnalKind; // Бюджетная классификация КОСГУ
    private String codeFSR; // КВФО
    private BigDecimal amount; // Сумма
    private String code; // Отраслевой код?
    private String kvr; // KVR

    private PayDocLines(String code, String kesr_code, String kesr_anal_kind, BigDecimal amount, String codeFSR, String kvr) {
        this.kesrCode = kesr_code;
        this.kesrAnalKind = kesr_anal_kind;
        this.codeFSR = codeFSR;
        this.amount = amount;
        this.code = code;
        this.kvr = kvr;
    }

    public PayDocLines(String code, String kesr_code, BigDecimal amount) {
        kesrCode = kesr_code;
        this.amount = amount;
        this.code = code;
    }

    public String getKesrCode() {
        if (kesrCode == null) return "";
        return kesrCode;
    }

    public String getKesrAnalKind() {
        return kesrAnalKind == null ? "" : kesrAnalKind;
    }

    public String getCodeFSR() {
        if (codeFSR == null) return "";
        return codeFSR;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCode() {
        return code;
    }

    public String getKvr() {
        return kvr;
    }

    public static ArrayList<PayDocLines> getLines(String codeSQL, Context context) throws SQLException {
        ArrayList<PayDocLines> lines = new ArrayList<>();
        PreparedStatement preparedStatement = context.prepareStatement(codeSQL);
        ResultSet RS = preparedStatement.executeQuery();
        while (RS.next()) {
            lines.add(new PayDocLines(RS.getString("CODE"),
                    RS.getString("KESR_CODE"),
                    RS.getString("ANAL_KIND"),
                    RS.getBigDecimal("AMOUNT"),
                    RS.getString("FSR_ID"),
                    RS.getString("KVR_CODE")));
        }
        return lines;
    }
}
