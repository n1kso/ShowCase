package com.obit.emc.docs;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.docs.Dictionaries.Respperson;
import com.obit.emc.docs.Dictionaries.UserCert;
import com.obit.emc.docs.additional.SpRLine;
import com.obit.emc.general.emcCustomDocument;
import org.w3c.dom.Element;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: MakKN
 * Date: 24.04.15
 * Time: 15:36
 * To change this template use File | Settings | File Templates.
 */
public class SpRas extends emcCustomDocument {
    public SpRas(Element task, Context con) throws UserException, SQLException {
        super(task, con);
        setMainSQL("select * from exporder where id=" + id);
        build();
    }

    @Override
    protected void getData() throws UserException, SQLException {
        orgId = mainRS.getString("recipient_id");
        _operTypeId = mainRS.getString("opertype_id");
        _orgAccId = mainRS.getString("orgaccount_id");

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

        //Строки документа
        String sql1 = "select * from exporder where title_id=" + id;
        ResultSet rs1 = execSQL(sql1);
        if (rs1 != null) {
//            _lines.add(new SpRLine(fTask, fcon, rs1.getString("OPERTYPE_ID"),
//                    rs1.getString("ORGACCOUNT_ID")));
            while (rs1.next()) {
                _lines.add(new SpRLine(fTask, fcon, rs1.getString("OPERTYPE_ID"),
                        rs1.getString("ORGACCOUNT_ID"),
                        rs1.getBigDecimal("AMOUNT")));
            }
        }
    }

    private final String tableName = "exporder";
    private ArrayList<UserCert> Certs = new ArrayList<>();
    private ArrayList<SpRLine> _lines = new ArrayList<>();
    // private Context fcon;
    private Element ftask;
    private String _operTypeId = "";
    private String _orgAccId = "";

    /**
     * ФИО руководителя из справочника ответственных лиц
     */
    private ArrayList<String> _org_head_FIO_ar = new ArrayList<>();
    /**
     * ФИО бухгалтера из справочника ответственных лиц
     */
    private ArrayList<String> _org_buh_FIO_ar = new ArrayList<>();
    private String orgId = "";

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

    public ArrayList<SpRLine> getLines() {
        return _lines;
    }

    public String get_operTypeId() {
        return _operTypeId;
    }

    public String getTableName() {
        return tableName;
    }

    public String get_orgAccId() {
        if (_orgAccId == null) _orgAccId = "";
        return _orgAccId;
    }

}
