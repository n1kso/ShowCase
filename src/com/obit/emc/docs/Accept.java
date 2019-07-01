package com.obit.emc.docs;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.general.emcCustomDocument;
import org.w3c.dom.Element;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class Accept extends emcCustomDocument {

    private final String tableName = "acceptorder";

    private Context fcon;
    private Element ftask;
    private String docID; // ID документа
    private String docNumber; // Номер документа
    private LocalDate docDate; // Дата документа
    private String orgID; // ID бюджетополучателя

    public Accept(Element task, Context con) throws UserException, SQLException {
        super(task, con);
        fcon = con;
        fTask = task;
        setMainSQL("SELECT * FROM BUDGORDER b WHERE b.DOCUMENT_ID IN (SELECT d.ID FROM DOCUMENT d WHERE ID IN (SELECT slave_id FROM ACCEPTORDERDOC a WHERE a.MASTER_ID IN (SELECT a1.DOCUMENT_ID FROM ACCEPTORDER a1 WHERE a1.ID=" + id + ")))");
        build();
    }

    @Override
    protected void getData() throws UserException, SQLException {

        orgID = mainRS.getString("ORG_ID");

        String sql = "select * from acceptorder where id=" + id;
        ResultSet rs = execSQL(sql);
        if (rs != null) {
            docID = rs.getString("document_id");
            docNumber = rs.getString("doc_number");
            docDate = mainRS.getTimestamp("DOC_DATE") != null ? mainRS.getTimestamp("DOC_DATE").toLocalDateTime().toLocalDate() : null;
        }
    }

    public String getDocID() {
        return docID;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public LocalDate getDocDate() {
        return docDate;
    }

    public String getOrgID() {
        return orgID;
    }

    public String getTableName() {
        return tableName;
    }
}
