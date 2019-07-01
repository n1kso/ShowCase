package com.obit.emc.checkers;

import com.obit.emc.Constants;
import com.obit.emc.docs.Dictionaries.*;
import com.obit.emc.docs.additional.IdPlat;
import com.obit.emc.docs.additional.Kbk;
import com.obit.emc.docs.additional.SpSSLine;
import com.obit.emc.docs.additional.kvdLine;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: KanMA
 * Date: 12.01.2012
 * Time: 14:28:07
 * To change this template use File | Settings | File Templates.
 */

public class MainChecker {

    /**
     * (3,8)
     * Проверять счет плательщика. Если закрыт, выдать сообщение "Лицевой счет учреждения закрыт"
     *
     * @param acc счет
     */
    static boolean isOrgAccountClose(Account acc) {
        if (acc == null || acc.getCloseDate() == null) return false;
        return acc.getCloseDate().isBefore(LocalDate.now());
    }

    /**
     * (4)Проверять, чтобы ID получателя не было пустое
     */
    static boolean CheckOrgIDIsNotNull(String orgID) {
        return orgID != null && !orgID.equals("");
    }

    /**
     * (5)Проверять, чтобы ID счета получателя не было пустое
     */
    static boolean CheckOrgAccIDIsNotNull(String orgAccID) {
        return orgAccID != null && !orgAccID.equals("");
    }

    /**
     * (6)
     * Сравнивать наименование получателя в заявке с кратким и официальным наименованиями в справочнике организаций.
     * Если ни с одним не совпадает, запретить обработку
     *
     * @param orgInDic     организация значение из справочника
     * @param orgNameInDoc наименование в контролируемом документе
     */
    static boolean CheckOrgNameCompareDict(Organization orgInDic, String orgNameInDoc) {
        if (orgInDic == null) return false;
        return (orgInDic.getOrgName().equals(orgNameInDoc) || orgInDic.getShortName().equals(orgNameInDoc));
    }

    /**
     * (7)
     * Сравнивать КПП получателя в заявке с КПП в справочнике организаций. Если не совпадает, запретить обработку
     *
     * @param orgInDic    организация значение из справочника
     * @param orgKppInDoc наименование в контролируемом документе
     */
    static boolean CheckOrgKppCompareDict(Organization orgInDic, String orgKppInDoc) {
        if (orgInDic == null) return false;
        if (orgKppInDoc == null) orgKppInDoc = "";

        return (orgInDic.getOrgKpp().equals(orgKppInDoc));
    }

    /**
     * (9)Проверять банки на активность
     */
    public static boolean CheckBankIsActive(Bank bank) {
        if (bank == null) return false;
        if (bank.getIsActive()) {
            return bank.getStatus().equals("ЛИКВ") ||
                    bank.getStatus().equals("ЗСЧТ") ||
                    bank.getStatus().equals("ОТЗВ") ||
                    bank.getStatus().equals("БЛОК") ||
                    bank.getStatus().equals("ИЗМР") ||
                    bank.getStatus().equals("ИСКЛ") ||
                    bank.getStatus().equals("ИНФО");
        }

        return false;
    }

    // Проверить на статус ВРФС - можно игнорировать
    public static boolean CheckIgnorableStatus(Bank bank) {
        if (bank == null) return false;
        if (bank.getIsActive()) return bank.getStatus().equals("ВРФС");

        return false;
    }

    /**
     * (10)Если тип БР смета или БУ то при сумме больше 100 000 должна быть ссылка на БО
     * и (КОСГУ не равны 21X, 24X, 26X и ДопЭК <>101)
     * Изменения 15.02.2012 новая постановка
     */
    public static boolean CheckAmountMore100k(EstKind estKind, BudgetObligation bo, Kbk kbk, BigDecimal amount) {
        BigDecimal oneHundredK = new BigDecimal(100000);
        if (estKind == null) return false;
        if ((estKind.getCaption().equals("Смета") || estKind.getCaption().equals("Бюджетное учреждение")) && (bo == null) && (amount.compareTo(oneHundredK) > 0 )) {
            return !kbk.getKESR_CODE().substring(0, 2).contains("21") &&
                    !kbk.getKESR_CODE().substring(0, 2).contains("24") &&
                    !kbk.getKESR_CODE().substring(0, 2).contains("26") &&
                    !kbk.getKDE_CODE().equals("101") &&
                    !kbk.getKDE_CODE().equals("510") &&
                    !kbk.getKVR_CODE().substring(0, 2).contains("83");
        }
        return false;
    }

    /**
     * (11)проверка Идентификатора платежа на корректное заполлнение
     */
    public static boolean CheckIdentPayOnCorrect(IdPlat idplat) {
        return (idplat != null && idplat.is_valid());
    }

    /**
     * Проверять соотвествие Доп ФК и кода видов доходов в идентификаторе. Соот. след: Доп ФК 01, КВД  9001    ДопФК 02, КВД 9003
     */
    public static boolean CheckKDFAndKVDInIP(Kbk kbk, IdPlat ip) {
        String kdf;
        if (kbk.getKDF_CODE().length() < 7)
            kdf = "0" + kbk.getKDF_CODE().substring(0, 3);
        else
            kdf = kbk.getKDF_CODE().substring(0, 4);

        if (ip.getPi_budgetcode() == null || ip.getPi_budgetcode().equals("") || ip.getPi_budgetcode().length() < 7)
            return false;

        String budg = ip.getPi_budgetcode().substring(3, 8);

        if (kdf.equals("1218") && !(kbk.getKDF_CODE().equals("1218150") || kbk.getKDF_CODE().equals("1218250")))
            return false;
        if (kdf.substring(0, 2).equals("01") && !budg.substring(0, 5).equals("90010"))
            return true;
        if (kdf.substring(0, 2).equals("02") && !budg.substring(0, 4).equals("9003"))
            return true;
        if (Pattern.compile("(02)|(03)|(04)|(12)").matcher(kdf.substring(0, 2)).find() &&
                !ip.getPi_budgetcode().substring(7,12).equals(kbk.getKDF_CODE().substring(0,5)))
            return true;
        if ((kdf.equals("1111") || kdf.equals("1114")) && !budg.equals("90015"))
            return true;
        if (kdf.equals("1108"))
            return !ip.getPi_budgetcode().substring(3, 11).equals("90031108");

        return false;
    }

    // (15) Б) Соответствие 3 последних знаков КВД (в 104 поле) и ДопКР - Чернобривец И.А.
    public static boolean Check_KVD_KDR(String kvd, String dopkr) {
        if (kvd.length() > 16) {
            String ss = kvd.substring(14, 17);
            if (ss.equals("000") && dopkr.equals("0")) return false;
            return !ss.equals(dopkr);
        }
        return false;
    }

    /* Чернобривец И.А.
     СпСС - 1 - Контролировать только выполнение действия "Сформировать уведомление об уточнении вида и принадлежности платежа"
     Если хоть в одной строке документа Ф > Р, выполнение команды запретить
    */
    public static boolean CheckFinExpense(ArrayList<SpSSLine> lines) {
        int k;
        if (lines.size() > 1) k = 1;
        else k = 0;
        for (int i = k; i < lines.size(); i++) {
            if (lines.get(i).get_Fin() > lines.get(i).get_Expense())
                return false;
        }
        return true;
    }


    /**
     * Контроль соответствия л/с отраслевому коду
     *
     * @param recAccount л/с получателя
     * @param _code        отраслевой код
     * @return true если отраслевой код не соответствует л/с
     */
    static boolean checkMatchAccountCode(String recAccount, String _code, Organization org) {
        Map<String, String> maskList = new HashMap<>();

        maskList.put("20902010005", "902(0{14})"); // Для невыясненных
        maskList.put("(2|3)\\S{9}8", "90\\S(0{14})"); // для СВР
        maskList.put("((20|30)\\S{8}1)", "\\S{13}41\\S\\S");
        maskList.put("((20|30)\\S{8}5)", "\\S{13}(22|(2[4-8]))\\S\\S");
        maskList.put("((21|31)\\S{9})", "\\S{13}(53|69)\\S\\S");

        if (_code.trim().equals("")) return false;

        for (Map.Entry<String, String> entry : maskList.entrySet()) {
            if (Pattern.compile(entry.getKey()).matcher(recAccount).find() && !Pattern.compile(entry.getValue()).matcher(_code).find())
                return true;
        }

        return Pattern.compile("41\\S{9}").matcher(recAccount).find() && Pattern.compile("\\S{13}69\\S\\S").matcher(_code).find() && !org.getRoles().contains("22");
    }

    //Чернобривец И.А.
    //Справка по расходам, проверка на наличие строк с отрицательной суммой в документе
    public static boolean CheckPositive(BigDecimal amount) {
        return amount.compareTo(new BigDecimal(0)) < 0;
    }

    //Проверка статуса лица на отстутсвие статусов перечисленных в массиве arrayAuthor
    static boolean CheckIdentPlatOnAuthor(String author) {
        String[] arrayAuthor = {"3", "9", "10", "11", "12", "13", "14", "16", "19", "20", "24"};
        return Arrays.asList(arrayAuthor).contains(author);
    }

    //Проверка вида платежа. Если не пустой то возвращать ложь
    public static boolean CheckPaykind(String paykind) {
        return !paykind.equals("");
    }

    // [10817] - Проверка заполнения поля ОКАТО
    static boolean checkOKTMO(String oktmo) {
        return oktmo.equals("") || (oktmo.equals("0") || oktmo.substring(0, 3).equals("000") || oktmo.equals("00000000") || oktmo.trim().length() != 8);
    }

    // [10818] - Контроль заполнения поля «Дата документа» в идентификаторе платежа
    static boolean checkDateInIdentPlat(String date) {
        return date.equals("") || !date.equals("0") && date.trim().length() != 10;
    }

    /**
     * Контроль заполнения поля «КБК». КБК не может быть заполнен «000 00000000000000000»;
     * для всех счетов КБК должен быть заполнен «0» или 20-ю знаками.
     *
     * @param KBK КБК
     * @return true при обнаружении ошибок
     */
    static boolean checkKBK(String KBK) {
        if (KBK.equals("000") || KBK.equals("00000000000000000000") || KBK.equals("")) return true;

        if (KBK.trim().length() == 1 && KBK.trim().equals("0")) return false;

        if (KBK.trim().length() != 20 || ((KBK.trim().length() == 1) && (!KBK.substring(0, 1).equals("0"))))
            return true;

        return KBK.substring(0, 3).equals("0  ") || KBK.substring(0, 3).equals("00 ") || KBK.length() != 20;

    }

    /**
     * Контроль заполнения поля «КБК» (для исключений). Для счетов  40101, 40701810300003000003,
     * 40701810700003000001, 40701810650041080012 КБК должен быть заполнен 20-ю знаками.
     *
     * @param KBK     КБК
     * @param account л/с
     * @return при ошибке вернет true
     */
    static boolean checkExceptionKBK(String KBK, String account) {
        if (Pattern.compile("40101\\d{15}").matcher(account).find() ||
                Pattern.compile("40701\\d{3}([37])\\d{10}([31])").matcher(account).find() ||
                account.equals("40701810650041080012")) {
            return !Pattern.compile("(?!000)\\d{3}\\d{17}").matcher(KBK).find();
        }
        return false;
    }

    static boolean checkIdentPlatAuthor(String account, String recAccountId) {

        if (account == null || account.trim().length() < 20) return false;

        return Pattern.compile("^40(302|101)").matcher(account).find() ||
                Pattern.compile("40501\\d{8}[1-2]\\d{6}").matcher(account).find() ||
                Pattern.compile("40([67])01\\d{8}([13])\\d{6}").matcher(account).find() ||
                Pattern.compile("40[5-7]03\\d{8}4\\d{6}").matcher(account).find() ||
                recAccountId.equals("1000004061");

    }

    /**
     * Поле "Статус лица, оформившего документ" идентификатора не может быть равно 00
     *
     * @param identPlat значение поля "Статус лица, оформившего документ" (author_id)
     * @return true при при выполнении условия
     */
    static boolean checkIdentPlatOn00(String identPlat) {
        return identPlat.equals("0");
    }

    /**
     * Проверка на наличие организации в списке, для которых не нужно проводить проверку на 2 ЭП
     *
     * @param c     объект класса Constants
     * @param idOrg id-к организации
     * @return true при наличии @idOrg в списке
     */
    public static Boolean checkOrgOnControlEDS(Constants c, String idOrg) {
        ArrayList<String> temp = null;
        try {
            temp = c.getListOrg();
        } catch (SQLException | ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }

        return temp != null && temp.contains(idOrg);
    }

    /**
     * Конртоль поля Статус Лица:
     * если поле АДМ в ИД платежа заполнено 18210102010011000110, то
     * статус лица должен быть 02;
     * если поле АДМ в ИД платежа начинается на 182..., то
     * статус лица должен быть 01;
     * @param piBudgetCode Идентификатор платежа
     * @param authorId Статус лица
     * @return Текст ошибки
     */
    static String checkAuthorIdOnTaxPayment (String piBudgetCode, String authorId) {
        if (piBudgetCode.equals("18210102010011000110") && !authorId.equals("2")) return "02-Налоговый агент";
        if (piBudgetCode.substring(0, 3).equals("182") && !piBudgetCode.equals("18210102010011000110") && !authorId.equals("1")) return "01-Налогоплательщик";
        return null;
    }

    /**
     * Контроль КДВ в идентификаторе платежа на закрытие
     * @param lines Коды видов дохода
     * @return True, если ошибка
     */
    static boolean isKvdClosed(ArrayList<kvdLine> lines) {
        boolean flag = false;
        for (kvdLine line: lines) {
            if (line.getEndDate() == null) return false;
            if (line.getEndDate().isBefore(LocalDate.now())) flag = true;
        }
        return flag;
    }

    /**
     * Контроль КДВ в идентификаторе платежа на наличие дочерних кодов
     * @param lines Коды видов дохода
     * @return True, если ошибка
     */
    static boolean isKvdHasChildrens(ArrayList<kvdLine> lines) {
        for (kvdLine line: lines) {
            if (line.getCode().substring(0,3).matches("(10)[1-9]") && line.getEndDate() == null)
                return true;
        }
        return false;
    }



    //Проверка на признак "Физ. лицо"
    static Boolean isPrivateFace(Organization org) {
        return org.getPersonage().equals("2");
    }
}