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
 * User: OvsAN
 * Date: 29.01.15
 * Time: 10:57
 * To change this template use File | Settings | File Templates.
 */
//Уведомление о поступлении средств по договору привлечения средств
public class UpsdEmc extends CustomControl {

    public void doBeforeAction(Context con, Element task, Element result) throws UserException, SQLException {
        String err = "";
        String act = task.getAttribute("action");

        if (act.equals("process") || act.equals("finalize") || act.equals("spawn_paydetail")) {
            Rvdp _Rvdp = new Rvdp(task, con);
            //№1143
            if ((_Rvdp.get_payTypeID().equals("1000000002")) && !_Rvdp.getOrgAccount().getAccNumber().equals("08902003562"))
                err += "* Должен быть указан счет с номером 08902003562 \n";
            if (!err.equals("")) {
                new EmcUserException(task, con).userExp(10270, "\n Обнаружены ошибки в документе № "
                        + _Rvdp.getDocNumber().trim() + " за " +  _Rvdp.getDocDateString() + ":\n" + err, _Rvdp.getTableName());
            }
        }
    }
}
