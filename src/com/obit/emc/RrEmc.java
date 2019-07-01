package com.obit.emc;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.bssys.server.processor.CustomControl;
import com.obit.emc.docs.Dictionaries.Purposefulgrant;
import com.obit.emc.docs.Rr;
import com.obit.emc.exception.EmcUserException;
import org.w3c.dom.Element;

import java.sql.SQLException;

/**
 * Created by MakKN on 22.05.2017.
 */
public class RrEmc extends CustomControl {
    public void doBeforeAction(Context con, Element task, Element result) throws UserException, SQLException {
        //Задача №1373

        if (task.getAttribute("action").equals("process") ||
                task.getAttribute("action").equals("process_register")) {
            Rr rr = new Rr(task, con);
            StringBuilder err = new StringBuilder();
            Purposefulgrant purposefulgrant = new Purposefulgrant(task, con, rr.getPurposefulgrant_id());
            if (purposefulgrant.get_controlfk_flag().equals("0")) {
                err.append("* У  Кода цели ").append(purposefulgrant.get_code()).append(" не выставлен признак \"Контроль в ФК\"");
            }
            for (int i = 0; i < rr.get_lines().size(); i++) {
                Purposefulgrant purposefulgrantfromlines = new Purposefulgrant(task, con, rr.get_lines().get(i).getPURPOSEFULGRANT_ID());
                if (purposefulgrantfromlines.get_controlfk_flag().equals("0"))
                    err.append("* У  Кода цели ").append(purposefulgrantfromlines.get_code()).append(" не выставлен признак \"Контроль в ФК\"");
            }
            if (!err.toString().equals("")) {
                new EmcUserException(task, con).userExp(10500, " №" + rr.getDoc_number().replaceAll(" ", "") + " Документ содержит следующие ошибки:\n" + err, rr.getTableName());
            }
        }
    }
}
