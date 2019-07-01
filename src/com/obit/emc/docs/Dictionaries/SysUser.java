package com.obit.emc.docs.Dictionaries;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.general.emcCustomDic;
import org.w3c.dom.Element;

import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: KanMA
 * Date: 30.01.2012
 * Time: 13:45:29
 * To change this template use File | Settings | File Templates.
 */
public class SysUser extends emcCustomDic {
    public SysUser(Element task, Context con) throws SQLException {
        super(task, con,"");
        setMainSQL("","sysuser","id=?",""+con.session.user_id);

        build();
    }

    @Override
    protected void getData() throws UserException, SQLException {
        if(mainRS==null) return;
        _orgID = mainRS.getString("org_id");
        _username = mainRS.getString("username");
    }


    private String _orgID = "0";
    private String _username = "0";

    public String getOrgID() {
        return _orgID;
    }
    public String getUserName() {
        return _username;
    }
}
