package com.obit.emc;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.bssys.server.processor.CustomControl;
import com.obit.emc.docs.Zvi;
import com.obit.emc.exception.EmcUserException;
import org.w3c.dom.Element;

import java.sql.SQLException;

/**
 * Created by ChesDV on 07.07.2017.
 */
public class ZviEmc extends CustomControl {
    public void doBeforeAction(Context con, Element task, Element result) throws UserException, SQLException {
        if (task.getAttribute("action").equals("process")) {
            String err = "";
            Zvi Zvi = new Zvi(task, con);

            if (Zvi.getDispStatus().equals("0")) {
                if (Zvi.Getpay_acc().equals("40302810825115000008") && !Zvi.Getoper_type().equals("58") && Zvi.Getkvi().equals("01061002040000550")) {
                    err = "Тип операции должен принимать значение: 58 «Выплаты из бюджета на 40302";
                }
            }
            if (!err.equals("")) {
                new EmcUserException(task, con).userExp(10800, " №" + Zvi.getDocNumber().trim() + " от " + " содержит следующие ошибки:\n" + err, Zvi.getTableName());
            }
        }
    }
}
