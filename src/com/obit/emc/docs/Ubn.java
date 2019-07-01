package com.obit.emc.docs;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.docs.additional.Kbk;
import com.obit.emc.docs.additional.UbnLine;
import com.obit.emc.general.emcCustomDocument;
import org.w3c.dom.Element;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @author InferNix
 * @date: 25.05.2009
 */
public class Ubn extends emcCustomDocument {

    private final String tableName = "EXPASSIGNMENT";
    private ArrayList<UbnLine> _lines = new ArrayList<>();

    public Ubn(Element task, Context con) throws UserException, SQLException {
        super(task, con);
        setMainSQL("select * from EXPASSIGNMENT where id = " + id + " or title_id = " + id);
        build();
    }

    @Override
    protected void getData() throws UserException, SQLException {
        // получаем данные
        Kbk kbk = new Kbk(mainRS.getString("KFSR_CODE"), mainRS.getString("KCSR_CODE"), mainRS.getString("KVR_CODE"),
                mainRS.getString("KESR_CODE"), mainRS.getString("KADMR_CODE"), mainRS.getString("KDF_CODE"),
                mainRS.getString("KDE_CODE"), mainRS.getString("KDR_CODE"), "");

        _lines.add(new UbnLine(fTask, fcon,
                mainRS.getString("FSR_ID"),
                mainRS.getString("OPERTYPE_ID"),
                mainRS.getString("ESTIMATE_ID"),
                mainRS.getString("MEMGROUP_CODE"), kbk,
                mainRS.getString("BUDGET_ID"),
                mainRS.getString("RECIPIENT_ID"),
                mainRS.getString("TITLE_ID"),
                mainRS.getString("ID"),
                mainRS.getString("ASSIGN_AMT1"),
                mainRS.getString("ASSIGN_AMT2"),
                mainRS.getString("ASSIGN_AMT3"),
                mainRS.getString("LIMIT_AMT1"),
                mainRS.getString("LIMIT_AMT2"),
                mainRS.getString("LIMIT_AMT3")));

        while (mainRS.next()) {
            kbk = new Kbk(mainRS.getString("KFSR_CODE"), mainRS.getString("KCSR_CODE"), mainRS.getString("KVR_CODE"),
                    mainRS.getString("KESR_CODE"), mainRS.getString("KADMR_CODE"), mainRS.getString("KDF_CODE"),
                    mainRS.getString("KDE_CODE"), mainRS.getString("KDR_CODE"), "");

            _lines.add(new UbnLine(fTask, fcon,
                    mainRS.getString("FSR_ID"),
                    mainRS.getString("OPERTYPE_ID"),
                    mainRS.getString("ESTIMATE_ID"),
                    mainRS.getString("MEMGROUP_CODE"), kbk,
                    mainRS.getString("BUDGET_ID"),
                    mainRS.getString("RECIPIENT_ID"),
                    mainRS.getString("TITLE_ID"),
                    mainRS.getString("ID"),
                    mainRS.getString("ASSIGN_AMT1"),
                    mainRS.getString("ASSIGN_AMT2"),
                    mainRS.getString("ASSIGN_AMT3"),
                    mainRS.getString("LIMIT_AMT1"),
                    mainRS.getString("LIMIT_AMT2"),
                    mainRS.getString("LIMIT_AMT3")));
        }
    }

    public ArrayList<UbnLine> GetLines() {
        return _lines;
    }
    public String getTableName() {
        return tableName;
    }



}
