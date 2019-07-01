package com.obit.emc;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.bssys.server.processor.CustomControl;
import com.obit.emc.checkers.GeneralChecker;
import com.obit.emc.docs.Rzss;
import com.obit.emc.func.func;
import org.w3c.dom.Element;

import java.sql.SQLException;

public class RzssEmc extends CustomControl {

    public void doBeforeAction(Context con, Element task, Element result) throws UserException, SQLException {

        String doc = "Распоряжение на зачисление специальных средств";
        String act = task.getAttribute("action");

        if (!act.equals("finalize")) return;

        Rzss rzss = new Rzss(task, con);
        String[] paramBasic = new String[]{doc, rzss.getDocNumber().trim(), rzss.getDocDateString()};
        GeneralChecker generalChecker = new GeneralChecker(con, task, paramBasic);

        // [10802] Контроль типа операции в зависимости от л/с (Задачи №914, 1425)
        generalChecker.checkOperTypeID(rzss.getOrgAccount().getAccountBalance(), rzss.getOperTypeID(), rzss.getOrgAccount(), rzss.getTableName());
    }
}
