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
/*Запрещает обработку документа, если на вкладке "График выплат" не выставлен признак
"Контролирует платежи на строгое соответствие графику выплат" с сообщением
"Не выставлен признак "Контролировать платежи на строгое соответствие графику выплат". */
public class SopsEmc extends CustomControl {
    public void doBeforeAction(Context con, Element task, Element result) throws UserException, SQLException {

        String act = task.getAttribute("action");
        if (!act.equals("prepare")) return;

        StringBuilder err = new StringBuilder();
        Sops sops = new Sops(task, con);

        //Задача №1103
        //Контролируем бюджетные строки в графике выплат на заполнение счета учредителя
        for (StageBudget c : sops.GetStageBudgetLines()) {
            if (c.getOrgaccount_id() == null || c.getOrgaccount_id().equals("")) {
                err.append("* Этап оплаты №").append(c.getStageNumber()).append(", строка №").append(c.getLineNumber()).append(": не указан Счет учредителя \n");
            }
        }
        if (!err.toString().equals("")) {
            new EmcUserException(task, con).userExp(10210, "Документ содержит следующие ошибки: \n" + err, sops.getTableName());
        }

    }
}