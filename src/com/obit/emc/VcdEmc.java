package com.obit.emc;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.bssys.server.processor.CustomControl;
import com.obit.emc.docs.Vcd;
import com.obit.emc.exception.EmcUserException;
import org.w3c.dom.Element;

import java.sql.SQLException;

/**
 * Created by chesdv on 05.07.2017.
 */
public class VcdEmc extends CustomControl {
    public void doBeforeAction(Context con, Element task, Element result) throws UserException, SQLException {

        if (task.getAttribute("action").equals("finalize")) {
            Vcd Vcd = new Vcd(task, con);

            if (Vcd.Getpay_acc().equals("02343003560") && !Vcd.Getoper_type().equals("58") && Vcd.Getkind_class().equals("1") && Vcd.Getrec_acc().equals("40302810825115000008")) {
                new EmcUserException(task, con).userExp(10600, " №" + Vcd.getDocNumber().trim()
                        + " от " + Vcd.getDocDateString()
                        + " содержит следующие ошибки:\n Тип операции должен принимать значение: 58 «Выплаты из бюджета на 40302", Vcd.getTableName());
            }
        }
    }
}
