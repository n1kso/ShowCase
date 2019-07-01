package com.obit.emc;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.bssys.server.processor.CustomControl;
import com.obit.emc.docs.Sops;
import com.obit.emc.docs.additional.StageBudget;
import com.obit.emc.exception.EmcUserException;
import org.w3c.dom.Element;

import java.sql.SQLException;


/**
 * Created with IntelliJ IDEA.
 * User: BelKA
 * Date: 17.07.13
 * Time: 11:12
 * To change this template use File | Settings | File Templates.
 */
/*��������� ��������� ���������, ���� �� ������� "������ ������" �� ��������� �������
"������������ ������� �� ������� ������������ ������� ������" � ����������
"�� ��������� ������� "�������������� ������� �� ������� ������������ ������� ������". */
public class SopsEmc extends CustomControl {
    public void doBeforeAction(Context con, Element task, Element result) throws UserException, SQLException {

        String act = task.getAttribute("action");
        if (!act.equals("prepare")) return;

        StringBuilder err = new StringBuilder();
        Sops sops = new Sops(task, con);

        //������ �1103
        //������������ ��������� ������ � ������� ������ �� ���������� ����� ����������
        for (StageBudget c : sops.GetStageBudgetLines()) {
            if (c.getOrgaccount_id() == null || c.getOrgaccount_id().equals("")) {
                err.append("* ���� ������ �").append(c.getStageNumber()).append(", ������ �").append(c.getLineNumber()).append(": �� ������ ���� ���������� \n");
            }
        }
        if (!err.toString().equals("")) {
            new EmcUserException(task, con).userExp(10210, "�������� �������� ��������� ������: \n" + err, sops.getTableName());
        }

    }
}