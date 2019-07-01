package com.obit.emc.checkers;

import com.bssys.server.Context;
import com.obit.emc.docs.AUBUReq;
import com.obit.emc.docs.Dictionaries.*;
import com.obit.emc.docs.PayDetail;
import com.obit.emc.docs.Zor;
import com.obit.emc.docs.additional.*;
import com.obit.emc.exception.EmcUserException;
import org.w3c.dom.Element;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.Calendar;

public class GeneralChecker {

    private Context con;
    private Element task;
    private String doc;
    private String docNumber;
    private String docDate;
    private String[] paramBasic;

    private EmcUserException emcUE;

    public GeneralChecker(Context con, Element task, String doc, String docNumber, String docDate) {
        this.con = con;
        this.task = task;
        this.doc = doc;
        this.docNumber = docNumber;
        this.docDate = docDate;
    }

    public GeneralChecker(Context con, Element task, String[] paramBasic) {
        this.con = con;
        this.task = task;
        this.paramBasic = paramBasic;
        this.docDate = paramBasic[2];
        this.emcUE = new EmcUserException(con, task);
    }


    /**
     * [10825] Контроль актуальности организации получателя
     * Проверки:
     * - организация значится актуальной;
     * - организация не значится закрытой на момент обработку ЭД
     *
     * @param organization организация получателя
     */
    public void checkActualOrg(Organization organization, String tableName) throws SQLException {
        EmcUserException emcUE = new EmcUserException(con, task);
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd.MM.yyyy");

        String curDate = new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());

        if (!organization.isActualOrg()) {
            emcUE.writeExp(10825, getParamBasic(organization.getShortName() + " значится неактуальной"));
        }

        if (organization.getCloseDate() != null) {
            if (LocalDate.parse(organization.getCloseDate(), f).isBefore(LocalDate.parse(curDate, f)) ||
                    LocalDate.parse(organization.getCloseDate(), f).isEqual(LocalDate.parse(curDate, f))) {
                emcUE.writeExp(10825, getParamBasic(organization.getShortName() + " закрыта " + organization.getCloseDate()));
            }
        }
        emcUE.GroupUserExp(tableName);
    }

    /**
     * [10802] - Контроль типа операции в зависимости от л/с (Задачи №914, 1425)
     *
     * @param acb  р/с
     * @param type тип операции
     * @param acc  л/с
     */
    public void checkOperTypeID(AccountBalance acb, String type, Account acc, String tableName) throws SQLException {
        String error = "";

        if (acb.getAccNumber().equals("40302810825115000008")) {
            if (type.equals("32")) {
                if (acc.getAccNumber().equals("05902003563"))
                    error = "Тип операции должен быть указан «57 – Перечисление в бюджет с 40302» или «58 - Выплаты из бюджета на 40302»";
            } else error = "Тип операции должен быть указан «32 – Операции по счету 40302»";
        }

        if (!error.equals("")) this.emcUE.userExp(10802, getParamBasic(error), tableName);
    }

    /**
     * [10810] - Контроль платежей в рамках одного банковского счета
     *
     * @param payKeeperAccID счет плательщика
     * @param recKeeperAccID счет получателя
     */
    public void checkPayOneAccount(String payKeeperAccID, String recKeeperAccID, String tableName) throws SQLException {
        if (payKeeperAccID != null) {
            if (payKeeperAccID.equals(recKeeperAccID))
                this.emcUE.userExp(10810, getParamBasic("ID-1: " + payKeeperAccID + " ID-2: " + recKeeperAccID), tableName);
        }
    }

    /**
     * [10826] Контроль объема вложенных файлов в ЗОР
     *
     * @param zor Объект класса Zor
     * @throws SQLException SQL исключение
     */
    public void checkFilesSize(Zor zor, String tableName) throws SQLException {
        int sum = 0;

        Purposefulgrant purposefulgrant = new Purposefulgrant(task, con, zor.getCodePurpose());

        if (purposefulgrant.getControlfkFlag().equals("1") && purposefulgrant.getPurposeFulGrantSourceID().equals("1")) {
            String sql = "SELECT * FROM DOCATTACHEX WHERE DOCUMENT_ID = " + zor.getDocumentId();
            PreparedStatement ps = this.con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                sum += rs.getInt("FILE_SIZE");
            }
        }
        if (sum > 9000000)
            emcUE.userExp(10826, new String[]{doc, docNumber, docDate, Double.toString(sum / 1024), Double.toString(9000000 / 1024)}, tableName);
    }

    /**
     * [10827] Контроль активности checkbox «Контроль ФК» для Целевых назначений
     *
     * @param codePurpose ID кода цели
     * @throws SQLException SQL исключение
     */
    public void checkActiveCheckboxControlFK(String codePurpose, String tableName) throws SQLException {
        Purposefulgrant purposefulgrant = new Purposefulgrant(task, con, codePurpose);

        if ((purposefulgrant.getPurposeFulGrantSourceID().equals("1") && purposefulgrant.get_controlfk_flag().equals("0")) ||
                (purposefulgrant.getPurposeFulGrantSourceID().equals("2") &&
                        purposefulgrant.getControlfkFlag().equals("0") &&
                        purposefulgrant.getCode().trim().length() == 20)) {
            emcUE.userExp(10827, new String[]{purposefulgrant.getCode(), purposefulgrant.getPurposeFulGrantSourceName()}, tableName);
        }

    }

    /**
     * [10828] Контроль активности счета организации (счет не должен быть закрыт)
     *
     * @param org      Плательщик
     * @param receiver Получатель
     * @param tableName Название таблицы
     */
    public void checkOrgAccountIsNotClose(Organization org, Organization receiver, String tableName) throws SQLException {
        if (MainChecker.isOrgAccountClose(org.getAcc()))
            this.emcUE.writeExp(10828, getParamBasic(org.getShortName()));
        if (MainChecker.isOrgAccountClose(receiver.getAcc()))
            this.emcUE.writeExp(10828, getParamBasic(receiver.getShortName()));
        this.emcUE.GroupUserExp(tableName);
    }

    /**
     * Проверки идентификатора платежа
     *
     * @param idPlat           идентификатор платежа
     * @param accountNumber    номер счета
     * @param accountUFKNumber номер счета УФК
     * @param org              Организация
     * @param recAccountId     ID счета получателя
     * @param tableName        Название таблицы
     */
    public void checkPaymentId(PaymentID idPlat, String accountNumber, String accountUFKNumber, Organization org, String recAccountId, String tableName) throws SQLException {
        this.emcUE.clearException();

        // [10819] Контроль заполнения КБК для исключений
        if (MainChecker.checkExceptionKBK(idPlat.getPiBudgetCode(), accountNumber) ||
                (accountUFKNumber != null && MainChecker.checkExceptionKBK(idPlat.getPiBudgetCode(), accountUFKNumber))) {
            if (!idPlat.checkForEmptiness())
                this.emcUE.writeExp(10819, paramBasic);
        }

        // [10822] Контроль заполнения поля «Статус лица, оформившего документ» для определенных счетов
        if (idPlat.getAuthorID().isEmpty() && idPlat.checkCountFillFields()) {
            if (MainChecker.checkIdentPlatAuthor(accountNumber, recAccountId)) {
                this.emcUE.writeExp(10822, getParamBasic(accountNumber));
            } else if (MainChecker.checkIdentPlatAuthor(accountUFKNumber, recAccountId)) {
                this.emcUE.writeExp(10822, getParamBasic(accountUFKNumber));
            }
        }

        if (!idPlat.checkForEmptiness()) {

            // [10843] Контроль заполнения идентификатора платежа для физических лиц, если не пустой, то ошибка
            if (MainChecker.isPrivateFace(org)) {
                this.emcUE.writeExp(10843, paramBasic);
            }
            // [10855] Проверка поля Тип платежа, при заполненом хотя бы одном другом поле в ИД платежа,
            // поле Тип платежа должно быть пустым
            if (!idPlat.getPayTypeCode().equals("")) {
                this.emcUE.writeExp(10855, paramBasic);
            }

            // [10816] Контроль заполнения полей идентификатора платежа
            if (idPlat.checkCountFillFields())
                this.emcUE.writeExp(10816, paramBasic);

            // [10817] Контроль заполнения поля ОКТМО
            if (MainChecker.checkOKTMO(idPlat.getPayOKATO()))
                this.emcUE.writeExp(10817, paramBasic);

            // [10818] Контроль заполнения поля «Дата документа» в идентификаторе платежа
            if (MainChecker.checkDateInIdentPlat(idPlat.getGrndDocDate()))
                this.emcUE.writeExp(10818, paramBasic);

            // [10815] Контроль заполнения поля «Статус лица, оформившего документ»
            if (MainChecker.CheckIdentPlatOnAuthor(idPlat.getAuthorID()))
                this.emcUE.writeExp(10815, getParamBasic(idPlat.getAuthorID()));

            // [10820] - Контроль заполнения КБК (кроме исключений)
            if (MainChecker.checkKBK(idPlat.getPiBudgetCode()))
                this.emcUE.writeExp(10820, paramBasic);

            // [10823] Контроль значения [00] в поле «Статус лица, оформившего документ»
            if (MainChecker.checkIdentPlatOn00(idPlat.getAuthorID()))
                this.emcUE.writeExp(10823, getParamBasic(idPlat.getAuthorID()));

            if (idPlat.getPiBudgetCode().length() == 20) {

                if (!Pattern.compile(".*\\D.*").matcher(idPlat.getPiBudgetCode().substring(3)).find()
                        && !Pattern.compile("[23]").matcher(idPlat.getPiAnalKind()).find()) {
                    if (kvdLine.getKvdLinesWithField("CODE", con, idPlat.getPiBudgetCode()).size() == 0) {
                        this.emcUE.writeExp(10848, paramBasic);
                    } else {
                        if (MainChecker.isKvdClosed(kvdLine.getKvdLinesWithField("CODE", con, idPlat.getPiBudgetCode())))
                            this.emcUE.writeExp(10851, paramBasic);
                    }
                    if (MainChecker.isKvdHasChildrens(kvdLine.getKvdLinesWithField("PARENT_CODE", con, idPlat.getPiBudgetCode()))) {
                        this.emcUE.writeExp(10845, paramBasic);
                    }
                }
                // [10850] Конртоль поля Статус Лица в идентификаторе платежа
                if (MainChecker.checkAuthorIdOnTaxPayment(idPlat.getPiBudgetCode(), idPlat.getAuthorID()) != null)
                    this.emcUE.writeExp(10850, new String[]{paramBasic[0], paramBasic[1], paramBasic[2],
                            MainChecker.checkAuthorIdOnTaxPayment(idPlat.getPiBudgetCode(), idPlat.getAuthorID())});
            }
        }
        this.emcUE.GroupUserExp(tableName);
    }

    /**
     * Проверки заполнения полей "Вид платежа", "Назначение платежа"
     *
     * @param paykind     вид платежа
     * @param description назначение платежа
     */
    public void checkBasicFieldPlat(String paykind, String description, String tableName) throws SQLException {
        this.emcUE.clearException();

        // [10812] Контроль заполнения вида платежа
        if (MainChecker.CheckPaykind(paykind))
            this.emcUE.writeExp(10812, paramBasic);

        // [10813] Контроль заполнения поля Назначение платежа
        if (description.equals(""))
            this.emcUE.writeExp(10813, paramBasic);

        this.emcUE.GroupUserExp(tableName);
    }

    /**
     * Контроли параметров организации
     *
     * @param org     организация
     * @param kpp     КПП
     * @param orgName наименование организации из документа
     * @param recID   ID получателя
     */
    public void checkParamOrg(Organization org, String kpp, String orgName, String recID, String tableName) throws SQLException {
        this.emcUE.clearException();

        // [10803] Контроль на соответствие наименования получателя с кратким и официальным наименованиями в справочнике организаций
        if (!MainChecker.CheckOrgNameCompareDict(org, orgName))
            this.emcUE.writeExp(10803, getParamBasic(new ArrayList<>(Arrays.asList(orgName, org.getShortName()))));

        // [10804] Контроль соответствия КПП получателя с КПП в справочнике организаций
        if (!MainChecker.CheckOrgKppCompareDict(org, kpp)) {
            ArrayList<String> params = new ArrayList<>(Arrays.asList(kpp, org.getShortName(), org.getOrgKpp()));
            this.emcUE.writeExp(10804, getParamBasic(params));
        }

        // [10807] - Контроль заполнения получателя (выбран из справочника)
        if (!MainChecker.CheckOrgIDIsNotNull(recID))
            this.emcUE.writeExp(10807, paramBasic);

        this.emcUE.GroupUserExp(tableName);
    }

    /**
     * [10829] Контроль возврата средств во временном распоряжении по КОСГУ 610 (Выбытие со счетов бюджета)
     *
     * @param documentID значение поля DOCUMENT_ID
     * @throws SQLException SQL исключение
     */
    public void checkReturnMoneyInTemporaryUse(String documentID, String tableName) throws SQLException {
        String sql = "SELECT FSR_ID, KESR_CODE, AMOUNT FROM AUBUCASHREQUEST WHERE DOCUMENT_ID=" + documentID;
        StringBuilder err = new StringBuilder("\n");
        PreparedStatement ps = this.con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            if (rs.getString("FSR_ID") != null && rs.getString("KESR_CODE") != null) {
                if (rs.getString("FSR_ID").equals("3") && !rs.getString("KESR_CODE").equals("610")) {
                    err.append("- в строке документа с суммой ").append(rs.getString("AMOUNT"))
                            .append(" указан КВФО 3 (Средства во временном распоряжении), но задан КОСГУ ").append(rs.getString("KESR_CODE")).append("\n");
                }
            }
        }
        if (!err.toString().equals("\n")) {
            err.append("Выбытие средств по КВФО 3 отражается по КОСГУ 610.");
            this.emcUE.userExp(10829, getParamBasic(err.toString()), tableName);
        }
    }

    /**
     * Проверка параметов банка (выбран из справочника, банк получателя активен)
     *
     * @param accountID ID счета
     * @param bank      Банк
     */
    public void checkParamsBank(String accountID, Bank bank, String tableName) throws SQLException {
        this.emcUE.clearException();

        // [10806] Контроль заполнения счета получателя (выбран из справочника)
        if (!MainChecker.CheckOrgAccIDIsNotNull(accountID))
            this.emcUE.writeExp(10806, paramBasic);

        // [10805] Контроль активности банка получателя
        if (MainChecker.CheckBankIsActive(bank))
            this.emcUE.writeExp(10805, getParamBasic(bank.getStatus()));

        // [10805] - Контроль банка на статус ВРФС - игнорируемый
        if (MainChecker.CheckIgnorableStatus(bank))
            this.emcUE.userExp(10805, getParamBasic(bank.getStatus()), true);

        this.emcUE.GroupUserExp(tableName);
    }

    /**
     * [10830] - Контроль выплат по зарплатным проектам на карты МИР
     *
     * @param org       Организация - получатель платежа
     * @param codeKESR  КОСГУ
     * @param paymentID Идентификатор платежа
     */
    public void checkPaymentCardHolder(Organization org, String codeKESR, PaymentID paymentID, String tableName) throws SQLException {
        Set<String> orgID = new HashSet<>(Arrays.asList("1000026924", "1000000655", "1000004611", "1000029343", "1000000304"));

        if (orgID.contains(org.getID())) {
            if ((codeKESR.equals("211") || codeKESR.equals("213")) && !paymentID.getPayTypeCode().trim().equals("1"))
                this.emcUE.userExp(10830, tableName);
            else if (!(codeKESR.equals("211") || codeKESR.equals("213")) && !paymentID.getPayTypeCode().trim().equals("1"))
                this.emcUE.userExp(10830, true, tableName);
        }
    }

    /**
     * [10832] – Контроль оплат за оказанные услуги
     *
     * @param req Объект документа Заявка БУ/АУ ВС
     */
    public void checkPayForService(AUBUReq req, String tableName) throws SQLException {
        StringBuilder error = new StringBuilder();

        if (req.getOpertypeID().equals("37") || req.getOpertypeID().equals("38")) {
            for (int i = 0; i < req.getLines().size(); i++) {
                if (req.getLines().get(i).getKesrCode().substring(0, 2).equals("22") &&
                        req.getKesrHeadCode().equals("130") &&
                        req.getFsrHeadID().equals("8") &&
                        req.getIdentPlatTest().checkForEmptiness())
                    error.append(" - строка: ").append(i).append(" - При оплате за услугу необходимо заполнить идентификатор платежа\n");
            }
        }

        if (!error.toString().equals("")) {
            this.emcUE.userExp(10832, getParamBasic(error.toString()), tableName);
        }
    }

    /**
     * [10836] – Контроль соответствия л/с отраслевому коду в строках ЭД БУ/АУ СУ
     *
     * @param payDetail Объект ЭД БУ/АУ СУ
     */
    public void checkMatchAccountIndustryCode(PayDetail payDetail, Organization org, String tableName) throws SQLException {
        String errorField = "\n Проверьте заполнение полей «Лицевой счет» и «ИНН».";
        StringBuilder error = new StringBuilder();

        try {
            // Уточняемые строки
            for (int i = 0; i < payDetail.getSourceLines().size(); i++) {
                if (!payDetail.getSourceLines().get(i).getRecAccount().equals("20902010005") &&
                        MainChecker.checkMatchAccountCode(payDetail.getSourceLines().
                                get(i).getRecAccount(), payDetail.getSourceLines().get(i).getIndustryCode(), org)) {
                    error.append(" -Уточняемая строка ").append(1 + i).append(": лицевой счет не соответствует отраслевому коду; \n");
                }
            }
            // Уточненные строки
            for (int i = 0; i < payDetail.getDstLines().size(); i++) {
                if (MainChecker.checkMatchAccountCode(payDetail.getDstLines().get(i).getRecAccount(), payDetail.getDstLines().get(i).getIndustryCode(), org))
                    error.append(" -Уточненная строка ").append(1 + i).append(": лицевой счет не соответствует отраслевому коду; \n");
            }
        } catch (NullPointerException e) {
            this.emcUE.userExp(10836, getParamBasic(errorField), tableName);
        }

        if (!error.toString().equals("")) this.emcUE.userExp(10833, getParamBasic(error.toString()), tableName);
    }

    /**
     * [10836] – Контроль соответствия л/с отраслевому коду БУ/АУ
     *
     * @param account л/с организации
     * @param line    массив строк AUBUReqLine
     */
    public void checkMatchAccountIndustryCode(String account, ArrayList<PayDocLines> line, Organization org, String tableName) throws SQLException {
        ArrayList<String> accountList = new ArrayList<>();
        ArrayList<String> industryCodeList = new ArrayList<>();

        for (PayDocLines curLine : line) {
            accountList.add(account);
            industryCodeList.add(curLine.getCode());
        }

        checkMatchAccountIndustryCode(accountList, industryCodeList, org, tableName);
    }

    /**
     * [10836] – Контроль соответствия л/с отраслевому коду
     *
     * @param accountList массив счетов
     * @param list        массив отраслевых кодов
     */
    private void checkMatchAccountIndustryCode(ArrayList<String> accountList, ArrayList<String> list, Organization org, String tableName) throws SQLException {
        String errorField = "\n вероятно, при заполнении полей допущена ошибка. Проверьте заполнение полей «Лицевой счет» и «Отраслевой код».";
        StringBuilder error = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            try {
                if (accountList.get(i).equals("20902010005")) return;
                if (MainChecker.checkMatchAccountCode(accountList.get(i), list.get(i), org))
                    error.append("\n - строка ").append(1 + i).append(": лицевой счет не соответствует отраслевому коду;");
            } catch (Exception e) {
                this.emcUE.userExp(10836, getParamBasic(errorField), tableName);
            }
        }
        if (!error.toString().equals("")) this.emcUE.userExp(10836, getParamBasic(error.toString()), tableName);
    }

    /**
     * [10834] – Контроль наличия файлов для КВФО 9
     *
     * @param doc   Документ
     * @param lines массив строк документа БУ/АУ ВС
     */
    public void checkAvailabilityFiles(Document doc, ArrayList<PayDocLines> lines, String tableName) throws SQLException {
        for (PayDocLines line : lines) {
            if (line.getCodeFSR().equals("9") && doc.getAttach_cnt().equals("0")) {
                this.emcUE.userExp(10834, paramBasic, tableName);
                break;
            }
        }
    }

    /**
     * [10836] – Контроль соответствия л/с отраслевому коду
     *
     * @param req Объект документа Заявка БУ/АУ ВС
     */
    public void checkMatchAccountCode(AUBUReq req, Organization org, String tableName) throws SQLException {
        String error = "";

        if (MainChecker.checkMatchAccountCode(req.getRecAccountNumber(), req.getIndustryCode(), org))
            error += " - лицевой счет получателя не соответствует отраслевому коду в классификации получателя.";

        if (!error.equals("")) this.emcUE.userExp(10836, getParamBasic(error), tableName);
    }

    /**
     * [10837] - Контроль заполнения классификации получателя
     *
     * @param req Объект класса БУ/АУ ВС
     */
    public void checkFillClassificRec(AUBUReq req, String tableName) throws SQLException {
        if (req.getRecAccountNumber().length() != 11) return;
        if (req.getRecUFKAccount().equals(req.getPayUFKAccount())) {
            if (req.getIndustryCode().equals("") ||
                    req.getKVRHeadCode().equals("") ||
                    req.getKSDAHeadCode().equals("") ||
                    req.getKFSRHeadCode().equals("") ||
                    req.getKesrHeadCode().equals("") ||
                    req.getFsrHeadID().equals("") ||
                    req.getGrandinvestmentHeadID().equals("")) this.emcUE.userExp(10837, paramBasic, tableName);
        }
    }

    /**
     * [10838] – Контроль на несовпадение счета УФК плательщика и счета получателя
     *
     * @param payUFKAccount Номер счета УФК плательщика
     * @param recAccount    Норме счета получателя
     */
    public void checkMatchPayAccountUFKRecAccount(String payUFKAccount, String recAccount, String tableName) throws SQLException {
        if (payUFKAccount.equals(recAccount)) this.emcUE.userExp(10838, paramBasic, tableName);
    }

    /**
     * [10839] - Контроль на несовпадение счета УФК и счета организации в справочнике
     *
     * @param account             Счет организации
     * @param recUFKAccountNumber Номер счета УФК
     * @param orgShortName        Наименование организации
     * @param blockName           Название блока
     */
    public void checkAccountTypeID(Account account, String recUFKAccountNumber, String orgShortName, String blockName, String tableName) throws SQLException {
        if (!recUFKAccountNumber.equals("") && account.getAccountBalance() != null) {
            String accNumberFinAgency = account.getAccountBalance().getAccNumber();
            if (account.getOrgAccType_id().equals("1") && !accNumberFinAgency.equals(recUFKAccountNumber)) {
                this.emcUE.userExp(10839, new String[]{account.getAccNumber(), recUFKAccountNumber, accNumberFinAgency, orgShortName, blockName}, tableName);
            }
        }
    }

    /**
     * [10844] - Контроль соответствия счетов плательщика и получателя из справочника с счетами на форме
     *
     * @param payAccount       Счет плательщика из справочника
     * @param recAccount       Счет получателя из справочника
     * @param payAccountNumber Счет плательщика
     * @param recAccountNumber Счет получателя
     */
    public void checkAccountNumbers(Account payAccount, Account recAccount, String payAccountNumber, String recAccountNumber, String tableName) throws SQLException {
        String err = "";
        if (!payAccount.getAccNumber().equals(payAccountNumber))
            err += "Лицевой счет плательщика " + payAccountNumber + " в заявке не соответствует выбранному счету " + payAccount.getAccNumber() + ". Перевыберите лицевой счет.\n";
        if (!recAccount.getAccNumber().equals(recAccountNumber))
            err += "Лицевой счет получателя " + recAccountNumber + " в заявке не соответствует выбранному счету " + recAccount.getAccNumber() + ". Перевыберите лицевой счет.\n";
        if (!err.equals(""))
            this.emcUE.userExp(10844, getParamBasic(err), tableName);
    }

    // [10846] Контроль заполнения поля Обязательство
    public void isRefExists(String kosgu, String kvr, String kvfo, String bo, String param, String org, BigDecimal sum, String tableName) throws SQLException {
        if (ListContracts.contains(new Key(kosgu, kvr, kvfo))) {
            if (((org.equals("1000000009") || org.equals("1000003677")) && sum.compareTo(new BigDecimal(400000)) > 0 && bo.equals("")) ||
                    ((org.equals("1000003676") || org.equals("1000000004") || org.equals("1000003732")) && sum.compareTo(new BigDecimal(100000)) > 0 && bo.equals("")))
                this.emcUE.userExp(10846, new String[]{paramBasic[0], paramBasic[1], paramBasic[2], kosgu, kvr, kvfo, param}, tableName);
        }
    }

    /**
     *  [10852] Контроль суммы исполнения
     * @param contractAmount Cумма контракта
     * @param lastYearAmount Исполнено на начало года
     * @param docAmount Сумма из заявки
     * @param rsvAmount  Сумма в процессе исполнения
     * @param fullAmount Сумма исполнено (Договор / Заявка БУ/АУ)
     * @param tableName Название таблицы
     */
    public void isNegativeAmount(BigDecimal contractAmount, BigDecimal lastYearAmount, BigDecimal docAmount, BigDecimal rsvAmount, BigDecimal fullAmount, String tableName) throws SQLException {
        BigDecimal amountInProcess = lastYearAmount.add(docAmount).add(rsvAmount).add(fullAmount);
        if (contractAmount.compareTo(amountInProcess) < 0) {
            this.emcUE.userExp(10852, new String[]{
                    paramBasic[0], paramBasic[1], paramBasic[2],
                    amountInProcess.toString(), docAmount.toString(), fullAmount.toString(),
                    rsvAmount.toString(), lastYearAmount.toString(), contractAmount.toString()}, tableName);
        }
    }

    /**
     * [10900] - Контроль времени обработки документа
     *
     * @param currentStatus      - текущий статус документа
     * @param forbiddenStatus    - запрещенный статус документа, на котором запрещать обработку документа
     * @param startForbiddenHour - время (часы) начала действия ограничения
     * @param endForbiddenHour   - время (часы) заверщение действия ограничения
     */
    public void checkForbiddenTime(String currentStatus, String forbiddenStatus, int startForbiddenHour, int endForbiddenHour, String tableName) throws SQLException {
        if (currentStatus.equals(forbiddenStatus)) {
            if (LocalDateTime.now().getHour() >= startForbiddenHour && LocalDateTime.now().getHour() < endForbiddenHour) {
                this.emcUE.userExp(10900, new String[]{paramBasic[0], String.valueOf(startForbiddenHour), String.valueOf(endForbiddenHour)}, tableName);
            }
        }
    }

    /**
     * Создает строку с дополнительным параметром ошибки
     *
     * @param newParam Новый параметр, который необходимо добавить к стандартной конфигурации
     */
    private String[] getParamBasic(String newParam) {
        String[] temp = new String[this.paramBasic.length + 1];
        System.arraycopy(this.paramBasic, 0, temp, 0, this.paramBasic.length);
        temp[this.paramBasic.length] = newParam;
        return temp;
    }

    /**
     * Создает строку с дополнительными параметроми ошибки
     *
     * @param newParam перечень параметров, которые необходимо добавить к стандартной конфигурации
     */
    private String[] getParamBasic(ArrayList<String> newParam) {
        String[] temp = new String[this.paramBasic.length + newParam.size()];
        System.arraycopy(this.paramBasic, 0, temp, 0, this.paramBasic.length);
        for (int i = 0; i < newParam.size(); i++) {
            temp[paramBasic.length + i] = newParam.get(i);
        }
        return temp;
    }
}
