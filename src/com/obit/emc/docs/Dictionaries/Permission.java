package com.obit.emc.docs.Dictionaries;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.general.emcCustomDic;
import org.w3c.dom.Element;

import java.sql.SQLException;

/**
 * Класс Разрешения
 * User: KanMA
 * Date: 12.01.2012
 * Time: 13:48:49
 * To change this template use File | Settings | File Templates.
 */
public class Permission extends emcCustomDic {

    public Permission(Element task, Context con, String id)  throws UserException, SQLException
    {
        super(task,con,id);
           if(id.equals("")) return;
           setMainSQL("","permission","id=?",id);
           build();
    }

    /**Счет разрешения*/
    public String getOrgAccountID() {
        return _orgAccountID;
    }

    @Override
    protected void getData() throws UserException, SQLException {
        _orgAccountID = mainRS.getString("orgaccount_id");        
    }

    private String _orgAccountID = "";


}
