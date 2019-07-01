package com.obit.emc;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.bssys.server.processor.CustomControl;
import com.obit.emc.docs.Uuvpp;
import com.obit.emc.exception.EmcUserException;
import org.w3c.dom.Element;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: CherIA
 * Date: 17.05.13
 * Time: 14:02
 * To change this template use File | Settings | File Templates.
 */
public class UuvppEmc extends CustomControl {
    public void doBeforeAction(Context con, Element task, Element result) throws UserException, SQLException {

        if (task.getAttribute("action").equals("insert") ||
                task.getAttribute("action").equals("update") ||
                task.getAttribute("action").equals("delete") ||
                task.getAttribute("action").equals("cbank_insert") ||
                task.getAttribute("action").equals("unregister") ||
                task.getAttribute("action").equals("decline") ||
                task.getAttribute("action").equals("before_rollback") ||
                task.getAttribute("action").equals("after_rollback") ||
                task.getAttribute("action").equals("defer") ||
                task.getAttribute("action").equals("manual_defer")) return;
        Uuvpp _uuvpp = new Uuvpp(task, con);
        StringBuilder err = new StringBuilder();
        for (int i = 0; i < _uuvpp.get_lines().size(); i++) {
            //Если хотя бы одна строка имеет все КБК=0, то выдавать сообщение об ошибке- Чернобривец И.А.
            if (_uuvpp.get_lines().get(i).getKFSR_CODE().equals("0") && _uuvpp.get_lines().get(i).getKCSR_CODE().equals("0") && _uuvpp.get_lines().get(i).getKVR_CODE().equals("0")
                    && _uuvpp.get_lines().get(i).getKESR_CODE().equals("0") && _uuvpp.get_lines().get(i).getKADMR_CODE().equals("0") && _uuvpp.get_lines().get(i).getKDF_CODE().equals("0")
                    && _uuvpp.get_lines().get(i).getKDE_CODE().equals("0") && _uuvpp.get_lines().get(i).getKDR_CODE().equals("0"))
                err.append("Строка ").append(i).append("\n");
        }
        if (!err.toString().equals("")) {
            new EmcUserException(task, con).userExp(10180, " В следующих целевых строках не заполнены КБК:\n" + err, _uuvpp.getTableName());
        }
    }

}
