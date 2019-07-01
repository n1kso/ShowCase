package com.obit.emc.docs.additional;

import com.bssys.server.Context;
import com.obit.emc.docs.Dictionaries.Account;
import org.w3c.dom.Element;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: cheria
 * Date: 12.03.14
 * Time: 12:01
 * To change this template use File | Settings | File Templates.
 */
public class PayDetailLine {

    private String FSR_ID; // КВФО
    private String recAccount; // Лицевой счет REC_ACCOUNT
    private String industryCode; // Отраслевой код
    private String kvr; // КВР
    private Account account;
    private String _code;
    private String _kesr_code; // КОСГУ
    private String _kesrAnalKind; // Бюджетная классификация КОСГУ
    private String _org_id;
    private String _acc_id;
    private String _fsr_id;
    private String conBudgId; // Обязательство id
    private String direction; // Направление операции

    public PayDetailLine(String org_id, String acc_id, String code, String kesr_code, String rec_account, String fsr_id, String kesrAnalKind, String direction, String kvr, String conBudgId, Context con, Element task) throws SQLException {
        _org_id = org_id;
        _acc_id = acc_id;
        _code = code;
        _kesr_code = kesr_code;
        _fsr_id = fsr_id;
        _kesrAnalKind = kesrAnalKind;
        this.direction = direction;
        this.kvr = kvr;
        this.conBudgId = conBudgId;

        FSR_ID = fsr_id;
        recAccount = rec_account;
        industryCode = code;
        if (acc_id != null)
            account = new Account(task, con, acc_id);
    }

    public String getOrgId() {
        if (_org_id == null) {
            _org_id = "";
            return _org_id;
        } else return _org_id;
    }

    public String getAccId() {
        if (_acc_id == null) {
            _acc_id = "";
            return _acc_id;
        } else return _acc_id;
    }

    public String getCode() {
        return _code;
    }

    public String getKesrCode() {
        return _kesr_code;
    }

    public String getFsrId() {
        return _fsr_id;
    }

    public String getFSR_ID() {
        return FSR_ID;
    }

    public String getRecAccount() {
        return recAccount;
    }

    public String getIndustryCode() {
        return industryCode;
    }

    public String get_kesrAnalKind() {
        return _kesrAnalKind;
    }

    public String getKvr() {
        return kvr;
    }

    public String getConBudgId() {
        return conBudgId == null ? "" : conBudgId;
    }

    public Account getAccount() {
        return account;
    }

    public String getDirection() {
        return direction != null ? direction : "";
    }

    public static ArrayList<PayDetailLine> getPayDetailLine(String codeSQL, Context context, Element task) throws SQLException {
        ArrayList<PayDetailLine> payDetailLines = new ArrayList<>();
        ResultSet rs = context.prepareStatement(codeSQL).executeQuery();
        while (rs.next()) {
            payDetailLines.add(new PayDetailLine(
                    rs.getString("REC_ID"),
                    rs.getString("REC_ACC_ID"),
                    rs.getString("CODE"),
                    rs.getString("KESR_CODE"),
                    rs.getString("REC_ACCOUNT"),
                    rs.getString("FSR_ID"),
                    rs.getString("CR_ANAL_KIND"),
                    rs.getString("CR_DIRECTION"),
                    rs.getString("KVR_CODE"),
                    rs.getString("conbudget_id"),
                    context, task));
        }
        return payDetailLines;
    }
}
