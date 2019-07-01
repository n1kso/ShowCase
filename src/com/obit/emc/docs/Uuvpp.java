package com.obit.emc.docs;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.docs.additional.UuvppLine;
import com.obit.emc.general.emcCustomDocument;
import org.w3c.dom.Element;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: CherIA
 * Date: 17.05.13
 * Time: 11:28
 * To change this template use File | Settings | File Templates.
 */
public class Uuvpp extends emcCustomDocument {

    private final String tableName = "paydetail";

    private ArrayList<UuvppLine> _lines = new ArrayList<>();

    public Uuvpp(Element task, Context con) throws UserException, SQLException {
        super(task, con);
        setMainSQL("select * from paydetail where id = " + id);
        build();
    }

    @Override
    protected void getData() throws UserException, SQLException {
        String sql = "select * from paydetailline where paydetail_id=" + id;
        ResultSet rs = execSQL(sql);
        if (rs != null) {
            while (rs.next()) {
                _lines.add(new UuvppLine(rs.getString("KFSR_CODE"), rs.getString("KCSR_CODE"), rs.getString("KVR_CODE"), rs.getString("KESR_CODE"), rs.getString("KADMR_CODE"), rs.getString("KDF_CODE"), rs.getString("KDE_CODE"), rs.getString("KDR_CODE")));
            }
        }
    }
    public ArrayList<UuvppLine> get_lines() {
        return _lines;
    }
    public String getTableName() {
        return tableName;
    }
}
