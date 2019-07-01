package com.obit.emc.docs.Dictionaries;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.general.emcCustomDic;
import org.w3c.dom.Element;

import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: KanMA
 * Date: 11.01.2012
 * Time: 10:49:12
 * Класс строки разрешения
 */
public class PermissionLine extends emcCustomDic {

    public PermissionLine(Element task, Context con, String id) throws UserException, SQLException
    {
        super(task,con,id);
        if(id.equals("")) return;
        setMainSQL("","permissionline","id=?",id);
        build();
    }

    /**Код источника средств*/
    public String getFundSourceID() {
        return _fundSourceID;
    }

    /**Возвращает родительское разрешение*/
    public Permission getParentPermission() {
        return _permission;
    }

    @Override
    protected void getData() throws UserException, SQLException {
       _fundSourceID =  mainRS.getString("fundsource_id");
       _permissionID = mainRS.getString("permission_id");
       _permission = new Permission(fTask,fcon,_permissionID);
       _estimateID = mainRS.getString("ESTIMATE_ID");
    }

    


    private String _fundSourceID = "";
    private String _permissionID = "";
    private Permission _permission = null;
    private String _estimateID;


    public String getEstimateID() {
        return _estimateID;
    }
}

