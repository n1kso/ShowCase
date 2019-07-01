package com.obit.emc.docs;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.docs.Dictionaries.Account;
import com.obit.emc.docs.additional.Kbk;
import com.obit.emc.docs.additional.UvsbLine;
import com.obit.emc.general.emcCustomDocument;
import org.w3c.dom.Element;

import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * @author InferNix
 * @date: 01.06.2009
 */
public class Uvsb extends emcCustomDocument {

    private final String tableName = "backorder";
    private ArrayList<UvsbLine> _lines = new ArrayList<>();
    private String _permission_id = "";
    private Date _docdate;
    private String _docNumber;
    private String _operTypeID = "";
    private String _documentID = "";
    private Account _orgAccount = null;
    private String _orgaccountID = "";
    private String _estimateID = "";
    private Kbk kbk = null;

    public Uvsb(Element task, Context con) throws UserException, SQLException {
        super(task, con);
        String sql = "select b.* ,  bo.INI_AMOUNT+BO.FUL_AMOUNT as amnt,bo.ID as IDBO from backorder b " +
                " left outer join Budgorder bo on (b.BUDGORDER_ID = bo.id and bo.DOCUMENTCLASS_ID = 10) " +
                " where b.id =  " + id + " or b.title_id =" + id;
        setMainSQL(sql);
        build();
    }

    public ArrayList<UvsbLine> getLines() {
        return _lines;
    }


    public String get_docdate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        return formatter.format(_docdate);
    }

    public String get_docNumber() {
        if (_docNumber == null) return "";
        return _docNumber;
    }

    public Account getOrgAccount() {
        return _orgAccount;
    }

    public String get_operTypeID() {
        return _operTypeID;
    }

    public String get_estimateID() {
        return _estimateID;
    }

    public String get_documentID() {
        return _documentID;
    }

    public String get_permission_id() {
        return _permission_id;
    }

    public Kbk getKbk() {
        return kbk;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    protected void getData() throws UserException, SQLException {
        _orgaccountID = mainRS.getString("ORGACCOUNT_ID");
        _operTypeID = mainRS.getString("OPERTYPE_ID");
        _documentID = mainRS.getString("DOCUMENT_ID");
        _estimateID = mainRS.getString("ESTIMATE_ID");
        if (mainRS.getString("permissionline_id") != null) _permission_id = mainRS.getString("permissionline_id");
        if (!_orgaccountID.equals("")) {
            _orgAccount = new Account(fTask, fcon, _orgaccountID);
        }
        kbk = new Kbk(mainRS.getString("KFSR_CODE"), mainRS.getString("KCSR_CODE"),
                mainRS.getString("KVR_CODE"), mainRS.getString("KESR_CODE"),
                mainRS.getString("KADMR_CODE"), mainRS.getString("KDF_CODE"),
                mainRS.getString("KDE_CODE"), mainRS.getString("KDR_CODE"),
                mainRS.getString("FSR_ID"));
        AddLine();
        while (mainRS.next()) {
            AddLine();
        }
    }

    private void AddLine() throws SQLException {
        boolean isHead = mainRS.getString("ID").equals(mainRS.getString("TITLE_ID"));
        _docdate = mainRS.getDate("DOC_DATE");
        _docNumber = mainRS.getString("DOC_NUMBER");
        _lines.add(
                new UvsbLine(mainRS.getString("IDBO"),
                        mainRS.getDouble("amnt"),
                        mainRS.getDouble("amount"), mainRS.getString("amount"), mainRS.getString("OPERTYPE_ID"), isHead));
    }

}
