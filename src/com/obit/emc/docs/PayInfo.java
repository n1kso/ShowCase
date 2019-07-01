package com.obit.emc.docs;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.general.emcCustomDocument;
import org.w3c.dom.Element;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: cheria
 * Date: 12.03.14
 * Time: 10:06
 * To change this template use File | Settings | File Templates.
 */
public class PayInfo extends emcCustomDocument {

    private final String tableName = "paymentinfo";

    private String _brothers_count = "";
    private String _parent_dispstatus = "";

    public PayInfo(Element task, Context con) throws UserException, SQLException {
        super(task, con);
        setMainSQL("", "paymentinfo", "id=?", id);
        build();
    }

    @Override
    protected void getData() throws UserException, SQLException {
        if (mainRS.getString("documentclass_id").equals("226")) {
            String sql = "Select * from document where id = (select parent_id from document where id=" + getDocumentId() + ")";
            ResultSet rs = execSQL(sql);
            if (rs != null) _parent_dispstatus = rs.getString("dispstatus_id");
        }
        String sql = "SELECT count(*) FROM document where documentclass_id=226 and dispstatus_id<>-1 and dispstatus_id<>108 and parent_id = (select parent_id from document where id=" + getDocumentId() + ")";
        ResultSet rs = execSQL(sql);
        if (rs != null) {
            _brothers_count = rs.getString("count(*)");
        }
    }

    public String getBrothersCount() {
        return _brothers_count;
    }

    public String GetParentDispstatus() {
        return _parent_dispstatus;
    }

    public String getTableName() {
        return tableName;
    }


}