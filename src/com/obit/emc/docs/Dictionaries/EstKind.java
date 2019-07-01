package com.obit.emc.docs.Dictionaries;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.general.emcCustomDic;
import org.w3c.dom.Element;

import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: KanMA
 * Date: 24.01.2012
 * Time: 12:54:51
 * To change this template use File | Settings | File Templates.
 */

/**
 * Типы бланков расхода*/
public class EstKind  extends emcCustomDic {


    public EstKind(Element task, Context con, String id) throws SQLException {
        super(task, con, id);
        setMainSQL("CAPTION,FINSOURCE_ID","estkind","id=?",id);
        build();
    }

    public String getCaption() {
        return _caption;
    }

    public String getFsrID() {
        return _fsrID;
    }
    
    @Override
    protected void getData() throws UserException, SQLException {
         _caption = mainRS.getString("CAPTION");
        _fsrID = mainRS.getString("FINSOURCE_ID");
    }


    private String _caption;
    private String _fsrID;


}
