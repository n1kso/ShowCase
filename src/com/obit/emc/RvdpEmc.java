package com.obit.emc;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.bssys.server.processor.CustomControl;
import com.obit.emc.docs.Rvdp;
import com.obit.emc.exception.EmcUserException;
import com.obit.emc.func.func;
import org.w3c.dom.Element;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: MakKN
 * Date: 12.11.14
 * Time: 10:57
 * To change this template use File | Settings | File Templates.
 */
//Распоряжение на выплату по договору привлечения средств
public class RvdpEmc extends CustomControl {
    public void doBeforeAction(Context con, Element task, Element result) throws UserException, SQLException {
        String err = "";
        String act = task.getAttribute("action");
        if (act.equals("payorder")) {
            Rvdp _Rvdp = new Rvdp(task, con);
            //1141
            if (_Rvdp.get_payTypeID().equals("1000000001") && !_Rvdp.getOrgAccount().getAccNumber().equals("08902003562"))
                err += "* Должен быть указан счет с номером 08902003562 \n";
            if (!err.equals("")) {
                new EmcUserException(task, con).userExp(10260, "\n Обнаружены ошибки в документе № " + _Rvdp.getDocNumber().trim() + " за " +  _Rvdp.getDocDateString() + ":\n"
                        + err, _Rvdp.getTableName());
            }
        }
    }
}

