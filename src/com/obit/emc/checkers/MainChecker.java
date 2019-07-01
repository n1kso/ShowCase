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
     * ��������� ���� �����������. ���� ������, ������ ��������� "������� ���� ���������� ������"
     *
     * @param acc ����
     */
    static boolean isOrgAccountClose(Account acc) {
        if (acc == null || acc.getCloseDate() == null) return false;
        return acc.getCloseDate().isBefore(LocalDate.now());
    }

    /**
     * (4)���������, ����� ID ���������� �� ���� ������
     */
    static boolean CheckOrgIDIsNotNull(String orgID) {
        return orgID != null && !orgID.equals("");
    }

    /**
     * (5)���������, ����� ID ����� ���������� �� ���� ������
     */
    static boolean CheckOrgAccIDIsNotNull(String orgAccID) {
        return orgAccID != null && !orgAccID.equals("");
    }

    /**
     * (6)
     * ���������� ������������ ���������� � ������ � ������� � ����������� �������������� � ����������� �����������.
     * ���� �� � ����� �� ���������, ��������� ���������
     *
     * @param orgInDic     ����������� �������� �� �����������
     * @param orgNameInDoc ������������ � �������������� ���������
     */
    static boolean CheckOrgNameCompareDict(Organization orgInDic, String orgNameInDoc) {
        if (orgInDic == null) return false;
        return (orgInDic.getOrgName().equals(orgNameInDoc) || orgInDic.getShortName().equals(orgNameInDoc));
    }

    /**
     * (7)
     * ���������� ��� ���������� � ������ � ��� � ����������� �����������. ���� �� ���������, ��������� ���������
     *
     * @param orgInDic    ����������� �������� �� �����������
     * @param orgKppInDoc ������������ � �������������� ���������
     */
    static boolean CheckOrgKppCompareDict(Organization orgInDic, String orgKppInDoc) {
        if (orgInDic == null) return false;
        if (orgKppInDoc == null) orgKppInDoc = "";

        return (orgInDic.getOrgKpp().equals(orgKppInDoc));
    }

    /**
     * (9)��������� ����� �� ����������
     */
    public static boolean CheckBankIsActive(Bank bank) {
        if (bank == null) return false;
        if (bank.getIsActive()) {
            return bank.getStatus().equals("����") ||
                    bank.getStatus().equals("����") ||
                    bank.getStatus().equals("����") ||
                    bank.getStatus().equals("����") ||
                    bank.getStatus().equals("����") ||
                    bank.getStatus().equals("����") ||
                    bank.getStatus().equals("����");
        }

        return false;
    }

    // ��������� �� ������ ���� - ����� ������������
    public static boolean CheckIgnorableStatus(Bank bank) {
        if (bank == null) return false;
        if (bank.getIsActive()) return bank.getStatus().equals("����");

        return false;
    }

    /**
     * (10)���� ��� �� ����� ��� �� �� ��� ����� ������ 100 000 ������ ���� ������ �� ��
     * � (����� �� ����� 21X, 24X, 26X � ����� <>101)
     * ��������� 15.02.2012 ����� ����������
     */
    public static boolean CheckAmountMore100k(EstKind estKind, BudgetObligation bo, Kbk kbk, BigDecimal amount) {
        BigDecimal oneHundredK = new BigDecimal(100000);
        if (estKind == null) return false;
        if ((estKind.getCaption().equals("�����") || estKind.getCaption().equals("��������� ����������")) && (bo == null) && (amount.compareTo(oneHundredK) > 0 )) {
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
     * (11)�������� �������������� ������� �� ���������� �����������
     */
    public static boolean CheckIdentPayOnCorrect(IdPlat idplat) {
        return (idplat != null && idplat.is_valid());
    }

    /**
     * ��������� ����������� ��� �� � ���� ����� ������� � ��������������. ����. ����: ��� �� 01, ���  9001    ����� 02, ��� 9003
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

    // (15) �) ������������ 3 ��������� ������ ��� (� 104 ����) � ����� - ����������� �.�.
    public static boolean Check_KVD_KDR(String kvd, String dopkr) {
        if (kvd.length() > 16) {
            String ss = kvd.substring(14, 17);
            if (ss.equals("000") && dopkr.equals("0")) return false;
            return !ss.equals(dopkr);
        }
        return false;
    }

    /* ����������� �.�.
     ���� - 1 - �������������� ������ ���������� �������� "������������ ����������� �� ��������� ���� � �������������� �������"
     ���� ���� � ����� ������ ��������� � > �, ���������� ������� ���������
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
     * �������� ������������ �/� ����������� ����
     *
     * @param recAccount �/� ����������
     * @param _code        ���������� ���
     * @return true ���� ���������� ��� �� ������������� �/�
     */
    static boolean checkMatchAccountCode(String recAccount, String _code, Organization org) {
        Map<String, String> maskList = new HashMap<>();

        maskList.put("20902010005", "902(0{14})"); // ��� ������������
        maskList.put("(2|3)\\S{9}8", "90\\S(0{14})"); // ��� ���
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

    //����������� �.�.
    //������� �� ��������, �������� �� ������� ����� � ������������� ������ � ���������
    public static boolean CheckPositive(BigDecimal amount) {
        return amount.compareTo(new BigDecimal(0)) < 0;
    }

    //�������� ������� ���� �� ���������� �������� ������������� � ������� arrayAuthor
    static boolean CheckIdentPlatOnAuthor(String author) {
        String[] arrayAuthor = {"3", "9", "10", "11", "12", "13", "14", "16", "19", "20", "24"};
        return Arrays.asList(arrayAuthor).contains(author);
    }

    //�������� ���� �������. ���� �� ������ �� ���������� ����
    public static boolean CheckPaykind(String paykind) {
        return !paykind.equals("");
    }

    // [10817] - �������� ���������� ���� �����
    static boolean checkOKTMO(String oktmo) {
        return oktmo.equals("") || (oktmo.equals("0") || oktmo.substring(0, 3).equals("000") || oktmo.equals("00000000") || oktmo.trim().length() != 8);
    }

    // [10818] - �������� ���������� ���� ����� ��������� � �������������� �������
    static boolean checkDateInIdentPlat(String date) {
        return date.equals("") || !date.equals("0") && date.trim().length() != 10;
    }

    /**
     * �������� ���������� ���� ���ʻ. ��� �� ����� ���� �������� �000 00000000000000000�;
     * ��� ���� ������ ��� ������ ���� �������� �0� ��� 20-� �������.
     *
     * @param KBK ���
     * @return true ��� ����������� ������
     */
    static boolean checkKBK(String KBK) {
        if (KBK.equals("000") || KBK.equals("00000000000000000000") || KBK.equals("")) return true;

        if (KBK.trim().length() == 1 && KBK.trim().equals("0")) return false;

        if (KBK.trim().length() != 20 || ((KBK.trim().length() == 1) && (!KBK.substring(0, 1).equals("0"))))
            return true;

        return KBK.substring(0, 3).equals("0  ") || KBK.substring(0, 3).equals("00 ") || KBK.length() != 20;

    }

    /**
     * �������� ���������� ���� ���ʻ (��� ����������). ��� ������  40101, 40701810300003000003,
     * 40701810700003000001, 40701810650041080012 ��� ������ ���� �������� 20-� �������.
     *
     * @param KBK     ���
     * @param account �/�
     * @return ��� ������ ������ true
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
     * ���� "������ ����, ����������� ��������" �������������� �� ����� ���� ����� 00
     *
     * @param identPlat �������� ���� "������ ����, ����������� ��������" (author_id)
     * @return true ��� ��� ���������� �������
     */
    static boolean checkIdentPlatOn00(String identPlat) {
        return identPlat.equals("0");
    }

    /**
     * �������� �� ������� ����������� � ������, ��� ������� �� ����� ��������� �������� �� 2 ��
     *
     * @param c     ������ ������ Constants
     * @param idOrg id-� �����������
     * @return true ��� ������� @idOrg � ������
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
     * �������� ���� ������ ����:
     * ���� ���� ��� � �� ������� ��������� 18210102010011000110, ��
     * ������ ���� ������ ���� 02;
     * ���� ���� ��� � �� ������� ���������� �� 182..., ��
     * ������ ���� ������ ���� 01;
     * @param piBudgetCode ������������� �������
     * @param authorId ������ ����
     * @return ����� ������
     */
    static String checkAuthorIdOnTaxPayment (String piBudgetCode, String authorId) {
        if (piBudgetCode.equals("18210102010011000110") && !authorId.equals("2")) return "02-��������� �����";
        if (piBudgetCode.substring(0, 3).equals("182") && !piBudgetCode.equals("18210102010011000110") && !authorId.equals("1")) return "01-����������������";
        return null;
    }

    /**
     * �������� ��� � �������������� ������� �� ��������
     * @param lines ���� ����� ������
     * @return True, ���� ������
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
     * �������� ��� � �������������� ������� �� ������� �������� �����
     * @param lines ���� ����� ������
     * @return True, ���� ������
     */
    static boolean isKvdHasChildrens(ArrayList<kvdLine> lines) {
        for (kvdLine line: lines) {
            if (line.getCode().substring(0,3).matches("(10)[1-9]") && line.getEndDate() == null)
                return true;
        }
        return false;
    }



    //�������� �� ������� "���. ����"
    static Boolean isPrivateFace(Organization org) {
        return org.getPersonage().equals("2");
    }
}