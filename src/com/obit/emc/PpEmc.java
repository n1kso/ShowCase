package com.obit.emc;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.bssys.server.processor.CustomControl;
import com.obit.emc.checkers.MainChecker;
import com.obit.emc.docs.Pp;
import com.obit.emc.exception.EmcUserException;
import com.obit.emc.func.func;
import org.w3c.dom.Element;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: OvsAN
 * Date: 27.01.15
 * Time: 11:10
 * To change this template use File | Settings | File Templates.
 */
public class PpEmc extends CustomControl {
    public void doBeforeAction(Context con, Element task, Element result) throws UserException, SQLException {
        String doc = "��������� ���������";
        String err = "";
        String act = task.getAttribute("action");

        //������� �� � ������ ��������
        if (act.equals("send") || act.equals("send_cash")) {
            Pp _pp = new Pp(task, con);
            EmcUserException emcUserException = new EmcUserException(task, con);
            //�1146
            if (_pp.getDescription() == null || _pp.getDescription().equals(""))
                err = "* �� ��������� ���������� ������� � ��������� ���������";
            // [10805] - �������� ���������� ����� ����������
            if (MainChecker.CheckBankIsActive(_pp.getBankPayee()))
                emcUserException.userExp(10805, new String[]{doc, _pp.getDocNumber().trim(), _pp.getDocDateString(), _pp.getBankPayee().getStatus()}, _pp.getTableName());
            // [10805] - �������� ����� �� ������ ���� - ������������
            if (MainChecker.CheckIgnorableStatus(_pp.getBankPayee()))
                emcUserException.userExp(10805, new String[]{doc, _pp.getDocNumber().trim(), _pp.getDocDateString(), _pp.getBankPayee().getStatus()}, true, _pp.getTableName());
            if (!err.equals("")) {
                emcUserException.userExp(10280, "\n ���������� ������ � ��������� � "
                        + _pp.getDocNumber().trim() + " �� " + _pp.getDocDateString() + ":\n"
                        + err + "� " + _pp.getDocNumber().trim() + " �� " +  _pp.getDocDateString(), _pp.getTableName());
            }
        }
    }
}
