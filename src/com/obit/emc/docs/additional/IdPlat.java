package com.obit.emc.docs.additional;
import com.bssys.server.UserException;
import com.bssys.server.Context;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLException;

/**
 * Идентификатор платежа
 *
 * @author KanMA
 */
public class IdPlat {

    public IdPlat(String author_id, String pi_anal_kind, String pi_budgetcode,
                  String pay_okato, String ground_id, String taxperiod,
                  String grnd_doc_number, String grnd_doc_date, Context con) {
        setAuthor_id(author_id);
        setPi_anal_kind(pi_anal_kind);
        setPi_budgetcode(pi_budgetcode);
        setPay_okato(pay_okato);
        setGround_id(ground_id);
        setTaxperiod(taxperiod);
        setGrnd_doc_number(grnd_doc_number);
        setGrnd_doc_date(grnd_doc_date);
        fcon = con;
    }

    /**
     * проверка правильности заполнения идентификатора платежа
     *
     * @return true если не заполнен вообще либо заполнены все поля
     */
    public boolean is_valid() {
        return (countFill == 0 || countFill == 7);
    }

    //статус лица
    public String getAuthorID() {
        return author_id;
    }

    public String getPi_anal_kind() {
        return pi_anal_kind;
    }

    public String getPi_budgetcode() {
        if (pi_budgetcode == null) {
            pi_budgetcode = "";
            return pi_budgetcode.trim();
        } else return pi_budgetcode.trim();
    }

    public String getPay_okato() {
        if (pay_okato == null) {
            pay_okato = "";
            return pay_okato;
        } else return pay_okato;
    }

    public String getGround_id() {
        return ground_id;
    }

    //Наименование показателя платежа
    public String getGroundName() throws SQLException {
        String sql = "select * from PAYGROUND where id=" + getGround_id();
        PreparedStatement ps = fcon.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        if (rs != null) {
            groundName = rs.getString("NAME");
        }
        return groundName;
    }

    public String getTaxperiod() {
        return taxperiod;
    }

    public String getGrnd_doc_number() {
        return grnd_doc_number;
    }

    public String getGrnd_doc_date() {
        if (grnd_doc_date == null) {
            grnd_doc_date = "";
            return grnd_doc_date;
        } else return grnd_doc_date;
    }

    public boolean checkOkato() throws SQLException {
        String sql = "select count(Okato) as q from territory where okato = " + pay_okato;
        PreparedStatement ps = fcon.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        boolean res = false;
        while (rs.next()) {
            res = rs.getInt("q") != 0;
        }
        return res;
    }

    /**
     * true если ИП пуст
     */
    public boolean IsEmpty() {
        return countFill <= 0;
    }

    private void setAuthor_id(String author_id) {
        if (author_id != null && !author_id.equals("")) {
            this.author_id = author_id;
            countFill++;
        }
    }

    private void setPi_anal_kind(String pi_anal_kind) {
        if (pi_anal_kind != null && !pi_anal_kind.equals("")) {
            this.pi_anal_kind = pi_anal_kind;
//      countFill++;
        }
    }

    private void setPi_budgetcode(String pi_budgetcode) {
        if (pi_budgetcode != null && !pi_budgetcode.trim().equals("")) {
            this.pi_budgetcode = pi_budgetcode;
            countFill++;
        }
    }

    private void setPay_okato(String pay_okato) {
        if (pay_okato != null && !pay_okato.equals("")) {
            this.pay_okato = pay_okato;
            countFill++;
        }
    }

    private void setGround_id(String ground_id) {
        if (ground_id != null && !ground_id.equals("")) {
            this.ground_id = ground_id;
            countFill++;
        }
    }

    private void setTaxperiod(String taxperiod) {
        if (taxperiod != null && !taxperiod.equals("")) {
            this.taxperiod = taxperiod;
            countFill++;
        }
    }

    private void setGrnd_doc_number(String grnd_doc_number) {
        if (grnd_doc_number != null && !grnd_doc_number.equals("")) {
            this.grnd_doc_number = grnd_doc_number;
            countFill++;
        }
    }

    private void setGrnd_doc_date(String grnd_doc_date) {
        if (grnd_doc_date != null && !grnd_doc_date.equals("")) {
            this.grnd_doc_date = grnd_doc_date;
            countFill++;
        }
    }

    private String groundName; // Показатель основания платежа
    private String author_id = "";
    private String pi_anal_kind = "";
    private String pi_budgetcode = "";
    private String pay_okato = "";
    private String ground_id = "";
    private String taxperiod = "";
    private String grnd_doc_number = "";
    private String grnd_doc_date = "";
    private int countFill = 0;
    private Context fcon;

    protected void getData() throws UserException, SQLException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
