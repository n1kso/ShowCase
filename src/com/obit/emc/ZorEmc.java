package com.obit.emc;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.bssys.server.processor.CustomControl;
import com.obit.emc.checkers.EDS;
import com.obit.emc.checkers.GeneralChecker;
import com.obit.emc.checkers.MainChecker;
import com.obit.emc.docs.Zor;
import com.obit.emc.exception.EmcUserException;
import org.w3c.dom.Element;

import java.sql.SQLException;

public class ZorEmc extends CustomControl {

    private Zor zor;

    public void doBeforeAction(Context con, Element task, Element result) throws UserException, SQLException {

        String doc = "Заявка на оплату расходов";

        if (task.getAttribute("action").equals("insert") ||
                task.getAttribute("action").equals("update") ||
                task.getAttribute("action").equals("delete") ||
                task.getAttribute("action").equals("cbank_insert") ||
                task.getAttribute("action").equals("unregister") ||
                task.getAttribute("action").equals("decline") ||
                task.getAttribute("action").equals("before_rollback") ||
                task.getAttribute("action").equals("after_rollback") ||
                task.getAttribute("action").equals("defer") ||
                task.getAttribute("action").equals("create") ||
                task.getAttribute("action").equals("manual_defer") ||
                task.getAttribute("action").equals("return")) return;

        zor = new Zor(task, con);
        EmcUserException emcUE = new EmcUserException(task, con);
        String[] paramBasic = new String[]{doc, zor.getDocNumber().trim(), zor.getDocDateString()};
        GeneralChecker generalCheck = new GeneralChecker(con, task, paramBasic);

        if (zor.getDispStatus().equals("1") && task.getAttribute("action").equals("process")) {
            if (zor.getContract() != null) {
                generalCheck.isNegativeAmount(zor.getContractAmount(), zor.getLastYearAmt(), zor.getAmount(), zor.getRsvAmount(), zor.getFullAmount(), zor.getTableName());
            }
        }

        // [10825] Контроль актуальности организации получателя
        generalCheck.checkActualOrg(zor.getReceiver(), zor.getTableName());

        // [10826] Контроль объема вложенных файлов в ЗОР
        generalCheck.checkFilesSize(zor, zor.getTableName());

        // [10827] Контроль активности checkbox «Контроль ФК» для Целевых назначений
        generalCheck.checkActiveCheckboxControlFK(zor.getCodePurpose(), zor.getTableName());

        // [10830] Контроль выплат по зарплатным проектам на карты МИР
        if (!(zor.getDispStatus().equals("54") || zor.getDispStatus().equals("56") || zor.getDispStatus().equals("13")
                || zor.getDispStatus().equals("17") || zor.getDispStatus().equals("10") || task.getAttribute("action").equals("return")
                || task.getAttribute("action").equals("return_acceptorder") || task.getAttribute("action").equals("exclude_acceptorder")))
            generalCheck.checkPaymentCardHolder(zor.getReceiver(), zor.getCodeKESR(), zor.getIdentPlatTest(), zor.getTableName());

        if (task.getAttribute("action").equals("accept") && zor.getDispStatus().equals("57")) {
            EDS eds = new EDS(zor.getDocumentId(), zor.getOrgID(), con, task, doc, zor.getDocNumber().trim(), zor.getDocDateString());
            eds.checkEDS(zor.getTableName());
        }

        // [10803] Контроль на соответствие наименования получателя с кратким и официальным наименованиями в справочнике организаций
        // [10804] Контроль соответствия КПП получателя с КПП в справочнике организаций
        // [10807] Контроль заполнения получателя (выбран из справочника)
        generalCheck.checkParamOrg(zor.getReceiver(), zor.getRecKpp(), zor.getRecName(), zor.getRecID(), zor.getTableName());

        // [10805] Контроль активности банка получателя
        // [10806] Контроль заполнения счета получателя (выбран из справочника)
        generalCheck.checkParamsBank(zor.getRecAccountID(), zor.getBankPayee(), zor.getTableName());

        // [10828] Контроль активности счета организации
        generalCheck.checkOrgAccountIsNotClose(zor.getOrg(), zor.getReceiver(), zor.getTableName());

        // [10809] - Контроль наличия ссылки на БО при сумме по 100 тыс. руб.
        if (MainChecker.CheckAmountMore100k(zor.getEst().GetEstKind(), zor.getBo(), zor.getKBK(), zor.getAmount()))
            emcUE.userExp(10809, paramBasic, zor.getTableName());

        // [10810] - Контроль платежей в рамках одного банковского счета
        generalCheck.checkPayOneAccount(zor.getOrgAccount().getKeeperAccID(), zor.getRecAccount().getKeeperAccID(), zor.getTableName());

        // [10811] - Контроль соответствия БИК в документе с БИК указанного счета
        if (!zor.getBIKdb().equals(zor.getBIC()) && zor.get_Acc_type().equals("2"))
            emcUE.userExp(10811, paramBasic, zor.getTableName());

        // [10812] - Контроль заполнения вида платежа
        // [10813] - Контроль заполнения поля Назначение платежа
        generalCheck.checkBasicFieldPlat(zor.getPaykind(), zor.getDescription(), zor.getTableName());

        // * Проверки идентификатора платежа
        // [10816] - Контроль заполнения полей идентификатора платежа
        // [10817] - Контроль заполнения поля ОКТМО
        // [10818] - Контроль заполнения поля «Дата документа» в идентификаторе платежа
        // [10815] - Контроль заполнения поля «Статус лица, оформившего документ»
        // [10819] - Контроль заполнения КБК для исключений
        // [10820] - Контроль заполнения КБК (кроме исключений)
        // [10822] - Контроль заполнения поля «Статус лица, оформившего документ» для определенных счетов
        // [10823] - Контроль значения [00] в поле «Статус лица, оформившего документ»
        if (task.getAttribute("action").equals("process") || task.getAttribute("action").equals("finalize")) {
                generalCheck.checkPaymentId(zor.getIdentPlatTest(), zor.getRecAccountNumber(), zor.getRecUFKAccountNumber(), zor.getReceiver(), zor.getRecAccountID(), zor.getTableName());
        }

        if ((zor.getKBK().getKESR_CODE().equals("241") || zor.getKBK().getKESR_CODE().equals("530")) &&
                !((zor.getKBK().getKVR_CODE().equals("623")) || zor.getKBK().getKVR_CODE().substring(0, 2).equals("81"))) {

            // [10821] - Контроль соответствия 3-х последних знаков КВД и ДопКР
            if (MainChecker.Check_KVD_KDR(zor.getIdentPlat().getPi_budgetcode(), zor.getKBK().getKDR_CODE()))
                emcUE.userExp(10821, paramBasic, zor.getTableName());

            // [10824] - Контроль соответствия ДопФК КВД
            if (MainChecker.CheckKDFAndKVDInIP(zor.getKBK(), zor.getIdentPlat()))
                emcUE.userExp(10824, new String[]{doc, zor.getDocNumber().trim(), zor.getDocDateString(), zor.getKBK().getKDF_CODE(), zor.getIdentPlat().getPi_budgetcode().substring(3)}, zor.getTableName());
        }

        // [10839] - Контроль на несовпадение счета УФК и счета организации в справочнике
        generalCheck.checkAccountTypeID(zor.getRecAccount(), zor.getRecUFKAccountNumber(), zor.getReceiver().getShortName(), "'Cчет' в блоке 'Расходование'", zor.getTableName());
    }
}


