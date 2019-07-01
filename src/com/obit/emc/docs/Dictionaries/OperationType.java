package com.obit.emc.docs.Dictionaries;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.general.emcCustomDic;
import org.w3c.dom.Element;

import java.sql.SQLException;

public class OperationType extends emcCustomDic {

    private String id;
    private String caption;


    public OperationType(Element task, Context con, String id) throws SQLException {
        super(task, con, id);
        setMainSQL("","opertype","id=?",id);
        build();
    }

    @Override
    protected void getData() throws UserException, SQLException {
        id = mainRS.getString("ID");
        caption = mainRS.getString("CAPTION");
    }

    public String getId() {
        return id;
    }

    public String getCaption() {
        return caption;
    }
}
