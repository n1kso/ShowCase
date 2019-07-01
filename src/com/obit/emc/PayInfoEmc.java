package com.obit.emc;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.bssys.server.processor.CustomControl;
import com.obit.emc.docs.PayInfo;
import com.obit.emc.exception.EmcUserException;
import org.w3c.dom.Element;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: cheria
 * Date: 12.03.14
 * Time: 10:13
 * To change this template use File | Settings | File Templates.
 */
public class PayInfoEmc extends CustomControl {
    public void doBeforeAction(Context con, Element task, Element result) throws UserException, SQLException {

        // Задача №1080
        if (task.getAttribute("action").equals("process")) {

            String err = "";
            PayInfo _PayInfo = new PayInfo(task, con);
            if (!_PayInfo.getBrothersCount().equals("1"))
                err += "* У родительского документа существует несколько Сведений.\n";
            if (!err.equals("")) {
                new EmcUserException( task, con).userExp(10230, " №" + _PayInfo.getDocNumber().trim() + " от " + _PayInfo.getDocDateString() + " содержит следующие ошибки:\n" + err, _PayInfo.getTableName());
            }
        }
    }
}
