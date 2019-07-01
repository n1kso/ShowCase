package com.obit.emc.docs;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.docs.Dictionaries.PermissionLine;
import com.obit.emc.general.emcCustomDocument;
import org.w3c.dom.Element;

import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: KanMA
 * Date: 11.01.2012
 * Time: 10:35:09
 * To change this template use File | Settings | File Templates.
 */
public class PayRequest extends emcCustomDocument {

    public PayRequest(Element task, Context con) throws UserException, SQLException {
        super(task, con);
        setMainSQL("", "payrequest", "id=?", id);
        build();
    }

    /**
     * Строка разрешения
     */
    public PermissionLine get_permission() {
        return _permissionLine;
    }

    @Override
    protected void getData() throws UserException, SQLException {
        if (!mainRS.getString("permissionline_id").equals("")) {
            _permissionLine = new PermissionLine(fTask, fcon, mainRS.getString("permissionline_id"));
        }
    }
    private PermissionLine _permissionLine;
}
