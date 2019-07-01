package com.obit.emc;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.bssys.server.processor.CustomControl;
import com.obit.emc.docs.AUBUExplainReq;
import org.w3c.dom.Element;

import java.sql.SQLException;


/**
 * Created by MakKN on 23.03.2017.
 */
public class AUBUExplainReqEmc extends CustomControl {
    public void doBeforeAction(Context con, Element task, Element result) throws UserException, SQLException {
//        String err = "";
//        if (task.getAttribute("action").equals("process")) {
//            Req = new AUBUExplainReq(task, con);
//            if (Req.getRec_account().equals("20902010005") && !Req.getOrgrole_id().equals("18"))
//                err += "* Неверно указан счет отправителя запроса.";
//            if (Req.getRec_account().equals("30902010005") && !Req.getOrgrole_id().equals("19"))
//                err += "* Неверно указан счет отправителя запроса.";
//        }
//
//        if (!err.equals(""))
//            con.throwUserException(new UserException(10161, " Документ содержит следующие ошибки:\n" + err));
    }

    private AUBUExplainReq Req;
}
