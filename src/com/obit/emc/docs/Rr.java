package com.obit.emc.docs;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.docs.additional.RrLine;
import com.obit.emc.general.emcCustomDocument;
import org.w3c.dom.Element;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by MakKN on 22.05.2017.
 */
public class Rr extends emcCustomDocument {

    private final String tableName = "EXPSCHEDULE";

    private String purposefulgrant_id = "";
    private String doc_number = "";
    private ArrayList<RrLine> _lines = new ArrayList<>();

    public Rr(Element task, Context con) throws UserException, SQLException {
        super(task, con);
        setMainSQL("select * from EXPSCHEDULE where id = " + id);
        build();
    }

    @Override
    protected void getData() throws UserException, SQLException {
        purposefulgrant_id = mainRS.getString("PURPOSEFULGRANT_ID");
        String sql = "SELECT * from EXPSCHEDULE where title_id=" + id;
        ResultSet rs = execSQL(sql);
        if (rs != null) {
            _lines.add(new RrLine(rs.getString("PURPOSEFULGRANT_ID")));
        }
    }
    public String getPurposefulgrant_id() {
        if (purposefulgrant_id == null || purposefulgrant_id.equals(""))
            return "";
        return purposefulgrant_id;
    }
    public String getDoc_number() {
        return doc_number;
    }
    public ArrayList<RrLine> get_lines() {
        return _lines;
    }
    public String getTableName() {
        return tableName;
    }
}
