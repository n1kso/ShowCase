package com.obit.emc.general;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import  org.w3c.dom.Element;

abstract public class emcCustomDocument extends emcCustomRoot {

    private LocalDate docDate;
    private String docNumber;
    private String documentId;
    private String dispStatus;
    private String action = "";
    private String documentClass = "";

    public emcCustomDocument(Element task, Context con ) {
        this.fTask = task;
        this.fcon = con;

        if (task!=null){
            this.id = task.getAttribute("ID");
            this.action = task.getAttribute("action");
        }
    }


    protected void build() throws SQLException, UserException {
        activate();
        getGeneralData();
        getData();
        deactivate();
    }

    protected void buildNotDocument() throws SQLException, UserException {
        activate();
        getData();
        deactivate();
    }

    private void getGeneralData() throws SQLException {
        docNumber = mainRS.getString("DOC_NUMBER");
        docDate = mainRS.getTimestamp("DOC_DATE").toLocalDateTime().toLocalDate();
        dispStatus = mainRS.getString("DISPSTATUS_ID");
        documentId = mainRS.getString("DOCUMENT_ID");
        documentClass = mainRS.getString("DOCUMENTCLASS_ID");
    }

    abstract protected void getData() throws UserException, SQLException;

    public LocalDate getDocDate() {
        return docDate;
    }

    public String getDocDateString() {
        return docDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    public String getDocNumber() {
        return docNumber;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getDispStatus() {
        return dispStatus;
    }

    public String getAction() {
        return action;
    }

    public String getDocumentClass() {
        return documentClass;
    }
}
