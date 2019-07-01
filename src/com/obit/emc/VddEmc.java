package com.obit.emc;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.bssys.server.processor.CustomControl;
import com.obit.emc.docs.Vdd;
import com.obit.emc.exception.EmcUserException;
import com.obit.emc.func.func;
import org.w3c.dom.Element;

import java.sql.SQLException;

/**
 * Created by makkn on 03.07.2017.
 * ВМК ВДД
 */
public class VddEmc extends CustomControl {
    public void doBeforeAction(Context con, Element task, Element result) throws UserException, SQLException {

        if (task.getAttribute("action").equals("spawn_payorder")) {
            String err = "";
            Vdd vdd = new Vdd(task, con);
            if (vdd.getDispStatus().equals("9")) {
                if (vdd.Getpay_account().equals("40302810825115000008") && !vdd.Getoper_type().equals("57")) {
                    err = "Тип операции должен принимать значение: 57 «Перечисление в бюджет с 40302»";
                }
            }
            if (!err.equals("")) {
                new EmcUserException(task, con).userExp(10500, " № " + vdd.getDocNumber().trim()
                        + " от " + vdd.getDocDateString()+ " содержит следующие ошибки:\n" + err, vdd.getTableName());
            }
        }
    }
}
