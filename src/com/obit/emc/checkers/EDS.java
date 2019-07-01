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

    private ArrayList<UserCert> Certs = new ArrayList<>(); // �����������, �������� �������� ��������
    private Person person;
    private EmcUserException emcUE;
    private String doc, docNumber, docDate;
    private Context con;

    /**
     * ����������� ������� ������
     *
     * @param docID     ID ���������
     * @param orgID     ID �����������
     * @param doc       �������� ���������
     * @param docNumber ����� ���������
     * @param docDate   ���� ���������
     * @throws SQLException ����������
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
            checkActualPersonOnSert(tableName); // �������� ������������ �������� �� ��� � ���. ���. ���
            checkEDSonPosssibleUse(tableName); // [10800] �������� ������� �������� �� ��� � ���. ���. ���
        }
    }

    // �������� ������������ �������� �� ��� � ���. ���. ���
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
            error += "- ��������� ������������� ����������� ������� ������������ �����������, ������� �� " +
                    "�������� ��� \"����������\"! � ����������� �����������\n";
        if (buhOrgNotAc > 0)
            error += "- ��������� ������������� ����������� ������� �������� ���������� �����������, ������� �� " +
                    "�������� ��� \"����������\"! � ����������� �����������\n";
        if (!error.equals("")) {
            emcUE.userExp(10800, new String[]{doc, docNumber, docDate, "\n" + error}, tableName);
        }
    }

    // [10800] �������� ������� �������� �� ��� � ���. ���. ���
    private void checkEDSonPosssibleUse(String tableName) throws SQLException {
        String error = "";
        int head_org = 0;
        int buh_org = 0;

        for (UserCert Cert : Certs) {
            if (getResult(person.getListHead("1"), Cert, "1000000411")) head_org++;
            if (getResult(person.getListBuh("1"), Cert, "1000000412")) buh_org++;
        }

        if (head_org == 0) error += " - ����������� ����������� ������� ������������ �����������\n";
        if (buh_org == 0) error += " - ����������� ����������� ������� �������� ����������\n";

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
