package com.obit.emc.checkers;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.docs.Dictionaries.Person;
import com.obit.emc.docs.Dictionaries.UserCert;
import com.obit.emc.exception.EmcUserException;
import org.w3c.dom.Element;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class EDS {

    private ArrayList<UserCert> Certs = new ArrayList<>(); // Сертификаты, которыми подписан документ
    private Person person;
    private EmcUserException emcUE;
    private String doc, docNumber, docDate;
    private Context con;

    /**
     * Конструктор объекта класса
     *
     * @param docID     ID документа
     * @param orgID     ID организации
     * @param doc       название документа
     * @param docNumber номер документа
     * @param docDate   дата документа
     * @throws SQLException исключение
     */
    public EDS(String docID, String orgID, Context con, Element task, String doc, String docNumber, String docDate) throws SQLException {

        String sql = "select certserialnumber,userrole_id, ISVALID " +
                "from digestsign " +
                "where digest_id in (select id from digest where document_id=" + docID + ")";
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Certs.add(new UserCert(task, con, rs.getString("certserialnumber"),
                    rs.getString("userrole_id"), rs.getString("isvalid")));
        }
        this.person = new Person(task, con, orgID);
        this.doc = doc;
        this.docNumber = docNumber;
        this.docDate = docDate;
        this.con = con;
        this.emcUE = new EmcUserException(task, con);
    }

    public void checkEDS(String tableName) throws UserException, SQLException {
        if (Certs.size() != 0) {
            checkActualPersonOnSert(tableName); // Проверка актульаности субъекта из ЭЦП в спр. Отв. лиц
            checkEDSonPosssibleUse(tableName); // [10800] Проверка наличия субъекта из ЭЦП в спр. Отв. лиц
        }
    }

    // Проверка актульаности субъекта из ЭЦП в спр. Отв. лиц
    private void checkActualPersonOnSert(String tableName) throws SQLException {
        String error = "";
        int headOrgNotAc = 0;
        int buhOrgNotAc = 0;

        for (UserCert Cert : Certs) {
            if (getResult(person.getListHead("0"), Cert, "1000000411") &&
                    !getResult(person.getListHead("1"), Cert, "1000000411"))
                headOrgNotAc++;
            if (getResult(person.getListBuh("0"), Cert, "1000000412") &&
                    !getResult(person.getListBuh("1"), Cert, "1000000412"))
                buhOrgNotAc++;
        }
        if (headOrgNotAc > 0)
            error += "- запрещено использование электронной подписи руководителя организации, который не " +
                    "значится как \"Актуальный\"! в справочнике организации\n";
        if (buhOrgNotAc > 0)
            error += "- запрещено использование электронной подписи главного бухгалтера организации, который не " +
                    "значится как \"Актуальный\"! в справочнике организации\n";
        if (!error.equals("")) {
            emcUE.userExp(10800, new String[]{doc, docNumber, docDate, "\n" + error}, tableName);
        }
    }

    // [10800] Проверка наличия субъекта из ЭЦП в спр. Отв. лиц
    private void checkEDSonPosssibleUse(String tableName) throws SQLException {
        String error = "";
        int head_org = 0;
        int buh_org = 0;

        for (UserCert Cert : Certs) {
            if (getResult(person.getListHead("1"), Cert, "1000000411")) head_org++;
            if (getResult(person.getListBuh("1"), Cert, "1000000412")) buh_org++;
        }

        if (head_org == 0) error += " - отсутствует электронная подпись руководителя организации\n";
        if (buh_org == 0) error += " - отсутствует электронная подпись главного бухгалтера\n";

        if (!error.equals("")) {
            emcUE.userExp(10800, new String[]{doc, docNumber, docDate, "\n" + error}, tableName);
        }
    }

    private static Boolean getResult(ArrayList<String> list, UserCert uc, String codeRole) {
        Boolean res = false;
        for (String aList : list) {
            if (uc.get_user_role_id().equals(codeRole) && uc.getFIO().equals(aList)) {
                res = true;
                break;
            }
        }
        return res;
    }

}
