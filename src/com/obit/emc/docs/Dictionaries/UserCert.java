package com.obit.emc.docs.Dictionaries;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.general.emcCustomDic;
import org.w3c.dom.Element;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: CherIA
 * Date: 23.05.13
 * Time: 14:10
 * To change this template use File | Settings | File Templates.
 */
public class UserCert extends emcCustomDic {

    private String isValid = "";
    private String _FIO = "";
    private String _user_role_id;
    private String _Block="";

    public UserCert(Element task, Context con,String _serial, String _role_id) throws SQLException {
        super(task, con,"");
        setMainSQL("","usercert","serialnumber=?",_serial);
        _user_role_id=_role_id;
        build();
    }

    public UserCert(Element task, Context con,String _serial, String _role_id, String isValid) throws SQLException {
        super(task, con,"");
        setMainSQL("","usercert","serialnumber=?",_serial);
        _user_role_id=_role_id;
        this.isValid = isValid;
        build();
    }

    @Override
    protected void getData() throws UserException, SQLException {
        if(mainRS==null) return;
        _FIO = mainRS.getString("ASSIGNEE");
        _Block = mainRS.getString("BLOCKED");

    }

    public String get_user_role_id()
    {
        return _user_role_id;
    }
    public String getFIO() {
        return _FIO == null? "" : _FIO;
    }
    public String getBlock() {
        return _Block;
    }
    public String getIsValid() {
        return isValid;
    }
}
