package com.obit.emc.docs.Dictionaries;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.general.emcCustomDic;
import org.w3c.dom.Element;

import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: KanMA
 * Date: 16.01.2012
 * Time: 10:47:18
 * To change this template use File | Settings | File Templates.
 */
public class Bank extends emcCustomDic {

    public Bank(Element task, Context con, String id) throws UserException, SQLException {
        super(task, con, id);
        setMainSQL("","bank","id=?",id);
        build();
    }

    /**@param bik БИК
     * @param name  не используется зарезервирован
     *  
     * */
    public Bank(Element task, Context con, String bik,String name) throws UserException, SQLException {
        super(task, con, "0");
        setMainSQL("","bank","bic=?",bik);
        build();
    }

    @Override
    protected void getData() throws UserException, SQLException {
        _isActive = mainRS.getInt("is_active");
        _status = mainRS.getString("status");

    }

    private int _isActive = 0;
    private String _status = "";

    /**возвращает true если банк активен*/
    public boolean getIsActive() {
        //return _isActive==1;
        return _isActive==1;
    }
    public String getStatus() {
        return  _status == null ? "" : _status;
    }
    
    
}
