/**package com.obit.emc;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.bssys.server.processor.CustomControl;
import com.obit.emc.checkers.EDS;
import com.obit.emc.checkers.GeneralChecker;
import com.obit.emc.checkers.MainChecker;
import com.obit.emc.docs.Znsss;
import com.obit.emc.exception.EmcUserException;
import com.obit.emc.func.func;
import org.w3c.dom.Element;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: CherIA
 * Date: 24.06.13
 * Time: 16:35
 * To change this template use File | Settings | File Templates.
 */
/**
public class ZnsssEmc extends CustomControl {
    public void doBeforeAction(Context con, Element task, Element result) throws UserException, SQLException {
        String doc = "������ �� �������� ����������� ������� � �������� �����";
        String err = "";
        String act = task.getAttribute("action");

        if (!act.equals("process")) return;

        EmcUserException emcUserException = new EmcUserException(con, task, "PAYREQUEST");
        Znsss _Znsss = new Znsss(task, con);

        EDS eds = new EDS(_Znsss.getDOCUMENT_ID(), _Znsss.getPayID(), con, task, doc, _Znsss.getDocNumber().trim(), func.DateConvert(_Znsss.getDocDate()));
        eds.checkEDS(_Znsss.getTableName());

        String[] paramBasic = new String[]{doc, _Znsss.getDocNumber().trim(), func.DateConvert(_Znsss.getDocDate())};
        GeneralChecker generalChecker = new GeneralChecker(con, task, paramBasic);

        // [10802] - �������� ���� �������� � ����������� �� �/� (������ �914, 1425)
        generalChecker.checkOperTypeID(_Znsss.getOrgAccount().getAccountBalance(), _Znsss.get_operTypeID(), _Znsss.getOrgAccount(), _Znsss.getTableName());

        // [10815] �������� ���������� ���� ������� ����, ����������� ��������
        // [10816] �������� ���������� ����� �������������� �������
        // [10817] �������� ���������� ���� �����
        // [10818] �������� ���������� ���� ����� ��������� � �������������� �������
        // [10819] �������� ���������� ��� ��� ����������
        // [10820] - �������� ���������� ��� (����� ����������)
        // [10822] �������� ���������� ���� ������� ����, ����������� �������� ��� ������������ ������
        // [10823] �������� �������� [00] � ���� ������� ����, ����������� ��������
        generalChecker.checkPaymentId(_Znsss.getIdentPlatTest(), _Znsss.getRecAccountNumber(), _Znsss.getRecUFKAccountNumber(), _Znsss.getReceiver(), _Znsss.getRecAccountID(), _Znsss.getTableName());

        // [10805] �������� ���������� ����� ����������
        if (MainChecker.CheckBankIsActive(_Znsss.getBankPayee()))
//            con.throwUserException(new UserException(10805, new String[]{doc, _Znsss.getDocNumber().trim(), func.DateConvert(_Znsss.getDocDate()), _Znsss.getBankPayee().getStatus()}, _Znsss.getTableName()));
            emcUserException.userExp(10805, new String[]{doc, _Znsss.getDocNumber().trim(), func.DateConvert(_Znsss.getDocDate()), _Znsss.getBankPayee().getStatus()}, _Znsss.getTableName());
        // [10805] �������� ����� �� ������ ���� - ������������
        if (MainChecker.CheckIgnorableStatus(_Znsss.getBankPayee()))
//            con.throwUserException(new UserException(10805, new String[]{doc, _Znsss.getDocNumber().trim(), func.DateConvert(_Znsss.getDocDate()), _Znsss.getBankPayee().getStatus()}, _Znsss.getTableName()), true);
            emcUserException.userExp(10805, new String[]{doc, _Znsss.getDocNumber().trim(),
                    func.DateConvert(_Znsss.getDocDate()), _Znsss.getBankPayee().getStatus()}, true, _Znsss.getTableName());
        // [10825] �������� ������������ ����������� ����������
        generalChecker.checkActualOrg(_Znsss.getReceiver(), _Znsss.getTableName());

        // [10828] �������� ���������� ����� �����������
        generalChecker.checkOrgAccountIsNotClose(_Znsss.getOrg(), _Znsss.getReceiver(), _Znsss.getDocDate(), _Znsss.getTableName());

        // �������� ���������� �������
        if (_Znsss.getDescription().equals(""))
            err += "* ���������� ������� ������ ���� ��������� \n";

        // 1349 �������� �������� ���� �������
        if (!MainChecker.CheckPaykind(_Znsss.getPaykind()))
            err += "* ��� ������� ������ ���� ������.\n";

        //�������� ���������� �������������� �������
        if (_Znsss.GetRecAccount().substring(0, 5).equals("40101")) {
            if (!MainChecker.CheckIdentPayOnCorrect(_Znsss.getIdentPlat()))
                err += "* �� ����� �������� ������������� ������� ��� ����� 40101. \n";
            //�������� ��������� ������ 1323
            if (_Znsss.getOktmoIdPl().equals("0") || _Znsss.getOktmoIdPl().trim().length() != 8)
                err += "* ��� ����� 40101 ���� ����� ������ �������� �� 8 ������. \n";

            if (_Znsss.getIdenPlat().equals(""))
                err += "* ��� ����� 40101 ���� \"������ ����\" ������ ���� ��������� ��������� �������� �� \"0\". \n";
        }

        if (!err.equals("")) {
//            con.throwUserException(new UserException(10190, "\n���������� ������ � ��������� � " +
//                _Znsss.getDocNumber().trim() + " �� " + DocDate + ":\n" + err));
            String DocDate = _Znsss.getDocDate().substring(8, 10) + "." + _Znsss.getDocDate().substring(5, 7) + "." + _Znsss.getDocDate().substring(0, 4);
            emcUserException.userExp(10190, "\n���������� ������ � ��������� � " +
                    _Znsss.getDocNumber().trim() + " �� " + DocDate + ":\n" + err, _Znsss.getTableName());
        }
    }
}
*/