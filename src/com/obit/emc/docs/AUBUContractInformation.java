package com.obit.emc.docs;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.docs.Dictionaries.Organization;
import com.obit.emc.docs.additional.SopsLine;
import com.obit.emc.docs.additional.StageBudget;
import com.obit.emc.general.emcCustomDocument;
import org.w3c.dom.Element;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AUBUContractInformation extends emcCustomDocument {

    private String tableName = "CONTRACT";

    private ArrayList<SopsLine> _lines = new ArrayList<>();
    private String contractType; // Вид договора
    private String payID;
    private Organization org;

    public AUBUContractInformation(Element task, Context con) throws UserException, SQLException {

        super(task, con);

        setMainSQL("select * from CONTRACT where id=" + id /*+ " or title_id=" + id + " order by id"*/);
        build();
    }

    @Override
    protected void getData() throws UserException, SQLException {

        contractType = mainRS.getString("Ci_Contracttype_Id");
        payID = mainRS.getString("PAY_ID");
        org = new Organization(fTask, fcon, payID);
        _lines.add(new SopsLine(fTask, fcon,
                mainRS.getDouble("FUL_AMOUNT"),
                mainRS.getDouble("AMOUNT"),
                mainRS.getString("NEED_CHECK_STAGES")));
        while (mainRS.next()) {
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

    public String getContractType() {
        return contractType == null ? "" : contractType;
    }

    public String getPayID() {
        return payID;
    }

    public Organization getOrg() {
        return org;
    }

    public String getTableName() {
        return tableName;
    }
}



