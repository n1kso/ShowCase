package com.obit.emc;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.bssys.server.processor.CustomControl;
import com.bssys.util.xml.XMLUtils;
import com.obit.emc.checkers.EDS;
import com.obit.emc.checkers.GeneralChecker;
import com.obit.emc.docs.AUBUReq;
import com.obit.emc.docs.Dictionaries.Document;
import com.obit.emc.docs.Dictionaries.OperationType;
import com.obit.emc.docs.additional.PayDocLines;
import com.obit.emc.docs.additional.Key;
import com.obit.emc.docs.additional.ListRolesAnalKind;
import com.obit.emc.exception.EmcUserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: CherIA
 * Date: 12.04.13
 * Time: 16:03
 * To change this template use File | Settings | File Templates.
 */
public class AUBUReqEmc extends CustomControl {

    private static final Logger log = LoggerFactory.getLogger(AUBUReqEmc.class);
    private EmcUserException emcUserException;

    private String doc = "Заявка БУ/АУ на выплату средств";

    public void doBeforeAction(Context con, Element task, Element result) throws UserException, SQLException {
        log.error("AUBUReqEmc {}", XMLUtils.getFormattedXMLText(task));

        if (task.getAttribute("action").equals("insert") ||
                task.getAttribute("action").equals("update") ||
                task.getAttribute("action").equals("delete") ||
                task.getAttribute("action").equals("cbank_insert") ||
                task.getAttribute("action").equals("unregister") ||
                task.getAttribute("action").equals("before_rollback") ||
                task.getAttribute("action").equals("after_rollback") ||
                task.getAttribute("action").equals("defer") ||
                task.getAttribute("action").equals("manual_defer") ||
                task.getAttribute("action").equals("return")) return;
        Req = new AUBUReq(task, con);
        emcUserException = new EmcUserException(task, con);

        String[] paramBasic = new String[]{doc, Req.getDocNumber().trim(), Req.getDocDateString()};
        GeneralChecker generalChecker = new GeneralChecker(con, task, paramBasic);

        // [10854] - Контроль действия "Сформировать распоряжение на зачисление средств на л/с"
        if (task.getAttribute("action").equals("spawn_moneytransfer") && Req.getDispStatus().equals("86") && !Req.getPayUFKAccount().equals(Req.getRecUFKAccount()))
            emcUserException.userExp(10854, paramBasic, Req.getTableName());

        // [10900] - Контроль времени обработки документа
        generalChecker.checkForbiddenTime(Req.getDispStatus(), "85", 12, 14, Req.getTableName());

        // [10853] - Контроль КОСГУ на признак ИСТОЧНИКИ при выбранном признаке БЕЗ ПРАВА РАСХОДОВАНИЯ
        if (checkKvfoOnSources())
            emcUserException.userExp(10853, paramBasic, Req.getTableName());

        // [10828] - Контроль активности счета организации
        generalChecker.checkOrgAccountIsNotClose(Req.getOrg(), Req.getReceiver(), Req.getTableName());

        if (task.getAttribute("action").equals("process") ||
                task.getAttribute("action").equals("accept")) {
            for (PayDocLines line: Req.getLines()) {
                generalChecker.checkPaymentCardHolder(Req.getReceiver(), line.getKesrCode(), Req.getIdentPlatTest(), Req.getTableName()); // [10830]
            }
        }

        // [10825] Контроль актуальности организации получателя
        generalChecker.checkActualOrg(Req.getReceiver(), Req.getTableName());

        // [10812] Контроль заполнения вида платежа
        // [10813] Контроль заполнения поля Назначение платежа
        generalChecker.checkBasicFieldPlat(Req.getPaykind(), Req.getDescription(), Req.getTableName());

        // [10803] Контроль на соответствие наименования получателя с кратким и официальным наименованиями в справочнике организаций
        // [10804] Контроль соответствия КПП получателя с КПП в справочнике организаций
        // [10807] Контроль заполнения получателя (выбран из справочника)
        generalChecker.checkParamOrg(Req.getReceiver(), Req.getRecKpp(), Req.getRecName(), Req.getRecID(), Req.getTableName());

        // [10829] Контроль возврата средств во временном распоряжении по КОСГУ 610 (Выбытие со счетов бюджета)
        generalChecker.checkReturnMoneyInTemporaryUse(Req.getDocumentId(), Req.getTableName());

        // [10805] Контроль активности банка получателя
        // [10806] Контроль заполнения счета получателя (выбран из справочника)
        generalChecker.checkParamsBank(Req.getRecAccountID(), Req.getBankPayee(), Req.getTableName());

        // Задача №1130 - Макогон
        if (task.getAttribute("action").equals("accept") || task.getAttribute("action").equals("docmentcheck") || task.getAttribute("action").equals("spawn_payorder")) {
            EDS eds = new EDS(Req.getDocumentId(), Req.getPayID(), con, task, doc, Req.getDocNumber().trim(), Req.getDocDateString());
            eds.checkEDS(Req.getTableName());
        }

        if (task.getAttribute("action").equals("process")) {
            // [10837] – Контроль заполнения классификации получателя
            generalChecker.checkFillClassificRec(Req, Req.getTableName());

            // [10840] - Контроль соответствия бюджетной классификации КОСГУ с типом операции документа
            checkOperation(task, con);

            // [10846] - Контроль ссылки на бюджетное обязательство
            String param = "";
            for (int i = 0; i < Req.getLines().size(); i++) {
                generalChecker.isRefExists(Req.getLines().get(i).getKesrCode(), Req.getLines().get(i).getKvr(), Req.getLines().get(i).getCodeFSR(), Req.getAcceptedOblId(), param, Req.getOrgID(), Req.getAmount(), Req.getTableName());
            }

            //Контроль суммы исполнения документа
            if (Req.getDispStatus().equals("1") && task.getAttribute("action").equals("process")) {
                if (Req.getContract() != null) {
                    generalChecker.isNegativeAmount(Req.getContractAmount(), Req.getLastYearAmt(), Req.get_amount(), Req.getRsvAmount(), Req.getFullAmount(), Req.getTableName());
                }
            }

            // [10816] - Контроль заполнения полей идентификатора платежа
            // [10817] - Контроль заполнения поля ОКТМО
            // [10818] - Контроль заполнения поля «Дата документа» в идентификаторе платежа
            // [10815] - Контроль заполнения поля «Статус лица, оформившего документ»
            // [10819] - Контроль заполнения КБК для исключений
            // [10820] - Контроль заполнения КБК (кроме исключений)
            // [10822] - Контроль заполнения поля «Статус лица, оформившего документ» для определенных счетов
            // [10823] - Контроль значения [00] в поле «Статус лица, оформившего документ»
            if (Req.getDispStatus().equals("1") || Req.getDispStatus().equals("0")) {
                if (Req.getRecUFKAccount().equals(Req.getPayUFKAccount()) && (Req.getPayID().equals(Req.getRecID()) || Req.getKesrAnalKind().equals("2"))) {
                    if (!Req.getIdentPlatTest().checkForEmptiness())
                        emcUserException.userExp(10816, paramBasic, Req.getTableName());
                } else {
                    generalChecker.checkPaymentId(Req.getIdentPlatTest(), Req.getRecAccountNumber(), Req.getRecUFKAccount(), Req.getReceiver(), Req.getRecAccountID(), Req.getTableName());
                }
            }

            // [10838] – Контроль на несовпадение счета УФК плательщика и счета получателя
            generalChecker.checkMatchPayAccountUFKRecAccount(Req.getPayUFKAccount(), Req.getRecAccountNumber(), Req.getTableName());

            // [10839] - Контроль на несовпадение счета УФК и счета организации в справочнике
            generalChecker.checkAccountTypeID(Req.getPayerAccount(), Req.getPayUFKAccount(), Req.getOrg().getShortName(), "'Счет' в блоке 'Плательщик'", Req.getTableName());
            generalChecker.checkAccountTypeID(Req.getRecAccount(), Req.getRecUFKAccount(), Req.getReceiver().getShortName(), "'Счет' в блоке 'Получатель'", Req.getTableName());
            generalChecker.checkAccountNumbers(Req.getPayerAccount(), Req.getRecAccount(), Req.getPayAccount(), Req.getRecAccountNumber(), Req.getTableName());
        }

        if (task.getAttribute("action").equals("process")) {

            // [10832] – Контроль оплат за оказанные услуги
            generalChecker.checkPayForService(Req, Req.getTableName());

            // [10833] – Контроль соответствия л/с отраслевому коду
            generalChecker.checkMatchAccountIndustryCode(Req.getPayAccount(), Req.getLines(), Req.getOrg(), Req.getTableName());

            // [10836] - Контроль соответствия л/с отраслевому коду
            generalChecker.checkMatchAccountCode(Req, Req.getOrg(), Req.getTableName());

            Doc = new Document(task, con, Req.getDocumentId());

            // ВМК [10834] – Контроль наличия файлов для КВФО 9
            generalChecker.checkAvailabilityFiles(Doc, Req.getLines(), Req.getTableName());
        }
    }

    private void checkOperation(Element task, Context con) throws SQLException {

        String mainUFKAccount = "40701810650041080012";
        Key key = null;

        if (Req.getPayUFKAccount().equals(mainUFKAccount) && Req.getRecUFKAccount().equals(mainUFKAccount)) {
            key = new Key(Req.getOrg().getRole(), Req.getReceiver().getRole(), Req.getOpertypeID());
        }

        if ((!Pattern.compile("18|19").matcher(Req.getReceiver().getRole()).find() ||
                !Pattern.compile("18|19").matcher(Req.getOrg().getRole()).find() ||
                Pattern.compile("22").matcher(Req.getReceiver().getRole()).find() ||
                Pattern.compile("22").matcher(Req.getOrg().getRole()).find()) && !Req.getOpertypeID().equals("0")) {
            emcUserException.userExp(10840, new String[]{doc, Req.getDocNumber().trim(),
                    Req.getDocDateString(), Req.getOrg().getShortName(), "должен быть указан тип операции 0 - НЕ УКАЗАНА"}, Req.getTableName());
        }

        if (key == null || ListRolesAnalKind.contains(key)) return;

        String recommendOperTypeId = ListRolesAnalKind.recommendTypeOperation(key);
        OperationType recommendOperType = recommendOperTypeId == null ? null : new OperationType(task, con, recommendOperTypeId);
        String recommend = recommendOperType == null ? "неверно указан тип операции" : "должен быть указан тип операции '" + recommendOperType.getId() + " - " + recommendOperType.getCaption() + "'";
        emcUserException.userExp(10840, new String[]{doc, Req.getDocNumber().trim(),
                Req.getDocDateString(), Req.getOrg().getShortName(), recommend}, Req.getTableName());
    }

    /**
     *
     * @return true - ошибка
     */
    private boolean checkKvfoOnSources() {
        for (PayDocLines line: Req.getLines()) {
            if (Req.getWoSpending() == 1 && line.getKesrAnalKind().equals("3")) {
                return true;
            }
        }
        return false;
    }
    private AUBUReq Req;
    private Document Doc;
    //Заявка БУ/АУ на выплату средств
}