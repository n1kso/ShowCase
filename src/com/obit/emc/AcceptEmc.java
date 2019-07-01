package com.obit.emc;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.bssys.server.processor.CustomControl;
import com.obit.emc.checkers.EDS;
import com.obit.emc.docs.Accept;
import com.obit.emc.func.func;
import org.w3c.dom.Element;

import java.sql.SQLException;

public class AcceptEmc extends CustomControl {

    private Accept AcceptD;

    public void doBeforeAction(Context con, Element task, Element result) throws UserException, SQLException {

        String doc = "Распоряжение на акцепт";

        if (task.getAttribute("action").equals("insert") ||
                task.getAttribute("action").equals("update") ||
                task.getAttribute("action").equals("delete") ||
                task.getAttribute("action").equals("cbank_insert") ||
                task.getAttribute("action").equals("unregister") ||
                task.getAttribute("action").equals("decline") ||
                task.getAttribute("action").equals("before_rollback")||
                task.getAttribute("action").equals("after_rollback") ||
                task.getAttribute("action").equals("defer") ||
                task.getAttribute("action").equals("create") ||
                task.getAttribute("action").equals("generate") ||
                task.getAttribute("action").equals("manual_defer")) return ;

        String act = task.getAttribute("action");

        AcceptD = new Accept(task, con);

        if(act.equals("register")) {
            EDS eds = new EDS(AcceptD.getDocID(), AcceptD.getOrgID(), con, task, doc, AcceptD.getDocNumber().trim(), AcceptD.getDocDateString());
            eds.checkEDS(AcceptD.getTableName());
        }
    }
}
