package com.obit.emc.docs;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.docs.Dictionaries.Respperson;
import com.obit.emc.docs.Dictionaries.UserCert;
import com.obit.emc.docs.additional.SpSSLine;
import com.obit.emc.general.emcCustomDocument;
import org.w3c.dom.Element;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: CherIA
 * Date: 19.03.13
 * Time: 10:38
 * To change this template use File | Settings | File Templates.
 */
public class SpSS extends emcCustomDocument {

    private final String tableName = "PAYREF";

    private Element ftask;
    private String orgId = "";
    private ArrayList<SpSSLine> _lines = new ArrayList<>();
    private ArrayList<String> _org_buh_FIO_ar = new ArrayList<>();
    private ArrayList<UserCert> Certs = new ArrayList<>();
    private ArrayList<String> _org_head_FIO_ar = new ArrayList<>();

    public SpSS(Element task, Context con) throws UserException, SQLException {
        super(task, con);
        setMainSQL(" select * from PAYREF where id=" + id + "or title_id=" + id + " order by id");
        build();
    }

    @Override
    protected void getData() throws UserException, SQLException {
        orgId = mainRS.getString("org_id");
        _lines.add(new SpSSLine(fTask, fcon,
                mainRS.getDouble("FIN_AMOUNT"),
                mainRS.getDouble("EXPENSE_AMOUNT"),
                mainRS.getString("ID"),
                mainRS.getString("TITLE_ID")));
        while (mainRS.next()) {
            _lines.add(new SpSSLine(fTask, fcon,
                    mainRS.getDouble("FIN_AMOUNT"),
                    mainRS.getDouble("EXPENSE_AMOUNT"),
                    mainRS.getString("ID"),
                    mainRS.getString("TITLE_ID")));
        }
        Respperson respperson = new Respperson(ftask, fcon, orgId);
        _org_buh_FIO_ar = respperson._org_buh_FIO_ar;
        _org_head_FIO_ar = respperson._org_head_FIO_ar;
        //ФИО руководителя из ЭП
        String sql = "select certserialnumber,userrole_id from digestsign where isvalid=1 and digest_id in (select id from digest where document_id=" + getDocumentId() + ")";
        ResultSet rs = execSQL(sql);
        if (rs != null) {
            Certs.add(new UserCert(ftask, fcon, rs.getString("certserialnumber"), rs.getString("userrole_id")));
            while (rs.next())
                Certs.add(new UserCert(ftask, fcon, rs.getString("certserialnumber"), rs.getString("userrole_id")));
        }
    }

    public ArrayList<SpSSLine> GetLines() {
        return _lines;
    }
    public String get_org_id() {
        return orgId;
    }
    public ArrayList<UserCert> get_Certs() {
        return Certs;
    }
    public ArrayList get_org_head_FIO_ar() {
        return _org_head_FIO_ar;
    }
    public ArrayList get_org_buh_FIO_ar() {
        return _org_buh_FIO_ar;
    }
    public String getTableName() {
        return tableName;
    }
}
