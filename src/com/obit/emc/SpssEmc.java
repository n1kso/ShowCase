package com.obit.emc;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.bssys.server.processor.CustomControl;
import com.obit.emc.checkers.MainChecker;
import com.obit.emc.docs.SpSS;
import com.obit.emc.exception.EmcUserException;
import org.w3c.dom.Element;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: CherIA
 * Date: 19.03.13
 * Time: 10:23
 * To change this template use File | Settings | File Templates.
 */
public class SpssEmc extends CustomControl {
    public void doBeforeAction(Context con, Element task, Element result) throws UserException, SQLException {
        String err = "";
        String act = task.getAttribute("action");
        SpSS spss = new SpSS(task, con);
        if (act.equals("spawn_paydetail")) {


                /* СпСС - 1- Контролировать только выполнение действия "Сформировать уведомление об уточнении вида и принадлежности платежа"
                Если хоть в одной строке документа Ф > Р, выполнение команды запретить */
            if (!MainChecker.CheckFinExpense(spss.GetLines()))
                err += "Документ содержит строки, резервирущие увелчичение остатка на л/с. Возможна только обработка до конечного статуса.\n";
        }
        if (act.equals("process")) {

            if (!spss.get_org_id().equals("1000000001")) {
                if (spss.get_Certs().size() != 0) {
                    int head_org = 0;
                    int buh_org = 0;
                    for (int i = 0; i < spss.get_Certs().size(); i++) {
                        int k = 0;
                        while (k < spss.get_org_head_FIO_ar().size()) {
                            if (spss.get_Certs().get(i).get_user_role_id().equals("1000000411") && spss.get_Certs().get(i).getFIO().equals(spss.get_org_head_FIO_ar().get(k))) {
                                head_org++;
                            }
                            k++;
                        }
                        int l = 0;
                        while (l < spss.get_org_buh_FIO_ar().size()) {
                            if (spss.get_Certs().get(i).get_user_role_id().equals("1000000412") && spss.get_Certs().get(i).getFIO().equals(spss.get_org_buh_FIO_ar().get(l))) {
                                buh_org++;
                            }
                            l++;
                        }

                    }

                    if (head_org == 0)
                        err += "* Уполномоченный представитель подписи руководителя отсутствует в справочнике ответственных лиц.\n";
                    if (buh_org == 0)
                        err += "* Уполномоченный представитель подписи главного бухгалтера отсутствует в справочнике ответственных лиц.\n";

                }
            }
        }
        if (!err.equals("")) {
            new EmcUserException(task, con).userExp(10150, " Документ содержит следующие ошибки:\n" + err, spss.getTableName());
        }
    }

}
