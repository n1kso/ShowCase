package com.obit.emc.docs;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.docs.additional.SopsLine;
import com.obit.emc.docs.additional.StageBudget;
import com.obit.emc.general.emcCustomDocument;
import org.w3c.dom.Element;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Created with IntelliJ IDEA.
 * User: BelKA
 * Date: 17.07.13
 * Time: 11:37
 * To change this template use File | Settings | File Templates.
 */
public class Sops extends emcCustomDocument {

    private final String tableName = "CONTRACT";

    private String _ClassId = "0";
    private ArrayList<SopsLine> _lines = new ArrayList<>();

    public Sops(Element task, Context con) throws UserException, SQLException {

        super(task, con);

        setMainSQL("select * from CONTRACT where id=" + id /*+ " or title_id=" + id + " order by id"*/);
        build();
    }

    @Override
    protected void getData() throws UserException, SQLException {

        _lines.add(new SopsLine(fTask, fcon,
                mainRS.getDouble("FUL_AMOUNT"),
                mainRS.getDouble("AMOUNT"),
                mainRS.getString("NEED_CHECK_STAGES")));
        while (mainRS.next()) {
            _ClassId = mainRS.getString("DOCUMENTCLASS_ID");
            _lines.add(new SopsLine(fTask, fcon,
                    mainRS.getDouble("FUL_AMOUNT"),
                    mainRS.getDouble("AMOUNT"),
                    mainRS.getString("NEED_CHECK_STAGES")));
        }

        String sql = "select * from stagebudget where contract_id=" + id;
        ResultSet rs = execSQL(sql);
        if (rs != null) {
            _stageBudgetLines.add(new StageBudget(rs.getString("STAGE_NUMBER"), rs.getString("LINE_NUMBER"), rs.getString("orgaccount")));
            while (rs.next()) {
                _stageBudgetLines.add(new StageBudget(rs.getString("STAGE_NUMBER"), rs.getString("LINE_NUMBER"), rs.getString("orgaccount")));
            }
        }
    }


    public ArrayList<SopsLine> GetLines() {
        return _lines;
    }
    private ArrayList<StageBudget> _stageBudgetLines = new ArrayList<>();
    public ArrayList<StageBudget> GetStageBudgetLines() {
        return _stageBudgetLines;
    }
    public String getTableName() {
        return tableName;
    }
}

