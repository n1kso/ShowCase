package com.obit.emc.docs.Dictionaries;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.general.emcCustomDic;
import org.w3c.dom.Element;

import java.sql.SQLException;

/**
 * Created by MakKN on 11.10.2016.
 */
public class Document extends emcCustomDic {

    public Document(Element task, Context con, String id) throws UserException, SQLException {
        super(task, con, id);
        setMainSQL("","document","id=?",id);
        build();
    }


    @Override
    protected void getData() throws UserException, SQLException {
        documentcassID = mainRS.getString("DOCUMENTCLASS_ID");
        opertypeID = mainRS.getString("OPERTYPE_ID");
        parentID = mainRS.getString("PARENT_ID");
        attach_cnt = mainRS.getString("ATTACH_CNT");
    }

    public String getDocumentClassID() {
        return documentcassID;
    }

    public String getOpertypeID() {
        return opertypeID;
    }

    public String getParentID() {
        return parentID;
    }

    public String getAttach_cnt() {
        return attach_cnt;
    }

    private String documentcassID = "";
    private String opertypeID = "";
    private String parentID = "";
    private String attach_cnt = "";
}
