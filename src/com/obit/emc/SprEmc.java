package com.obit.emc;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.bssys.server.processor.CustomControl;
import com.obit.emc.checkers.MainChecker;
import com.obit.emc.docs.Dictionaries.Account;
import com.obit.emc.docs.SpRas;
import com.obit.emc.exception.EmcUserException;
import org.w3c.dom.Element;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: CherIA
 * Date: 26.04.13
 * Time: 12:57
 * To change this template use File | Settings | File Templates.
 */
public class SprEmc extends CustomControl {
    public void doBeforeAction(Context con, Element task, Element result) throws UserException, SQLException {
        StringBuilder err = new StringBuilder();
        String act = task.getAttribute("action");

        if (act.equals("spawn_paydetail")) {
            // Задача №853
            /* Справка по расходам - 1- Контролировать только выполнение действия "Сформировать уведомление об уточнении вида и принадлежности платежа"
            Если хоть в одной строке документа отрицательная сумма, выполнение команды запретить */
            SpRas spras = new SpRas(task, con);
            if (spras.getLines().size() > 0) {
                for (int i = 0; i < spras.getLines().size(); i++) {
                    if (MainChecker.CheckPositive(spras.getLines().get(i).getAmount()))
                        err.append("* Строка: ").append(i).append(" - отрицательная сумма. Переход на статус 'Исполнение' приведет к необоснованному увеличению остатка кассового плана.\n");

                    else if (MainChecker.CheckPositive(spras.getLines().get(0).getAmount()))
                        err.append("* Документ содержит отрицательные суммы. Переход на статус 'Исполнение' приведет к необоснованному увеличению остатка кассового плана.\n");
                }
                if (!err.toString().equals(""))
                    new EmcUserException(task, con).userExp(10170, " Документ содержит следующие ошибки:\n" + err, spras.getTableName());
            }
        }
        if (act.equals("carry")) {
            SpRas spras = new SpRas(task, con);
            //Задача №1150
            // Соответствие ФИО субъекта из ЭП с ролью руководителя и ФИО руководителя, указанного в справочнике ответственных лиц для КУЦО 02, КУПО 05, КУПРО 06 - Макогон
            if (spras.get_Certs().size() != 0) {
                int head_org = 0;
                int buh_org = 0;
                for (int i = 0; i < spras.get_Certs().size(); i++) {
                    int k = 0;
                    while (k < spras.get_org_head_FIO_ar().size()) {
                        if (spras.get_Certs().get(i).get_user_role_id().equals("1000000411") && spras.get_Certs().get(i).getFIO().equals(spras.get_org_head_FIO_ar().get(k))) {
                            head_org++;
                        }
                        k++;
                    }
                    k = 0;
                    while (k < spras.get_org_buh_FIO_ar().size()) {
                        if (spras.get_Certs().get(i).get_user_role_id().equals("1000000412") && spras.get_Certs().get(i).getFIO().equals(spras.get_org_buh_FIO_ar().get(k))) {
                            buh_org++;
                        }
                        k++;
                    }
                }

                if (head_org == 0)
                    err.append("* Уполномоченный представитель подписи руководителя отсутствует в справочнике ответственных лиц.\n");
                if (buh_org == 0)
                    err.append("* Уполномоченный представитель подписи главного бухгалтера отсутствует в справочнике ответственных лиц.\n");
            }
            // }
            //ВМК №1199

            if (!spras.get_orgAccId().equals("")) {
                Account account = new Account(task, con, spras.get_orgAccId());
                if ((spras.get_operTypeId().equals("10") || spras.get_operTypeId().equals("53")) && !(account.getOrgAccType_id().equals("5")))
                    err.append("* Тип операции не соотвествует типу лицевого счета плательщика. \n");
                if (account.getOrgAccType_id().equals("5")) {
                    if (!spras.get_operTypeId().equals("53") && !spras.get_operTypeId().equals("10"))
                        err.append("* Тип операции не соотвествует типу лицевого счета плательщика. \n");
                }
            }

            for (int i = 0; i < spras.getLines().size(); i++) {
                if (!spras.getLines().get(i).get_orgAccId().equals("")) {
                    Account account = new Account(task, con, spras.getLines().get(i).get_orgAccId());
                    if ((spras.getLines().get(i).get_operTypeId().equals("10") || spras.getLines().get(i).get_operTypeId().equals("53"))
                            && !(account.getOrgAccType_id().equals("5")))
                        err.append("* Строка").append(i).append(". Тип операции не соотвествует типу лицевого счета плательщика. \n");
                    if (account.getOrgAccType_id().equals("5")) {
                        if (!spras.getLines().get(i).get_operTypeId().equals("53") && !spras.getLines().get(i).get_operTypeId().equals("10"))
                            err.append("* Строка").append(i).append(". Тип операции не соотвествует типу лицевого счета плательщика. \n");
                    }
                }
            }
            if (!err.toString().equals(""))
                new EmcUserException(task, con).userExp(10170, " №" + spras.getDocNumber().trim() + " Документ содержит следующие ошибки:\n" + err, spras.getTableName());
        }
    }
}