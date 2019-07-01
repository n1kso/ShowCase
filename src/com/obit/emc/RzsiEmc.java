package com.obit.emc;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.bssys.server.processor.CustomControl;
import com.obit.emc.docs.Rzsi;
import com.obit.emc.exception.EmcUserException;
import org.w3c.dom.Element;

import java.sql.SQLException;

/**
 * Created by ChesDV on 06.07.2017.
 */
public class RzsiEmc extends CustomControl {
    public void doBeforeAction(Context con, Element task, Element result) throws UserException, SQLException {

        if (task.getAttribute("action").equals("finalize")) {
            String err = "";
            Rzsi Rzsi = new Rzsi(task, con);
            if (Rzsi.getDispStatus().equals("1")) {
                if (Rzsi.Getkvi().equals("01061002040000550") && !Rzsi.Getoper_type().equals("57") && Rzsi.Getpay_acc().equals("40302810825115000008")) {
                    err = "Тип операции должен принимать значение: 57 «Перечисление в бюджет с 40302";
                }
            }
            if (!err.equals("")) {
                new EmcUserException(task, con).userExp(10700, " №" + Rzsi.getDocNumber().trim() + " от " + " содержит следующие ошибки:\n" + err, Rzsi.getTableName());
            }
        }
    }
}
