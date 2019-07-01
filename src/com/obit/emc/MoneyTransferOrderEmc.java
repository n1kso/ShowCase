package com.obit.emc;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.bssys.server.processor.CustomControl;
import com.obit.emc.checkers.GeneralChecker;
import com.obit.emc.docs.Dictionaries.*;
import com.obit.emc.docs.MoneyTransferOrder;
import com.obit.emc.docs.additional.Key;
import com.obit.emc.docs.additional.ListRolesAnalKind;
import com.obit.emc.exception.EmcUserException;
import com.obit.emc.func.func;
import org.w3c.dom.Element;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MoneyTransferOrderEmc extends CustomControl {

    private MoneyTransferOrder _mto;
    private String doc = "������������ �� ���������� ������� �� �/�";
    private EmcUserException emcUserException;

    public void doBeforeAction(Context con, Element task, Element result) throws UserException, SQLException {


        if (!task.getAttribute("action").equals("finalize")) {
            return;
        }

        emcUserException = new EmcUserException(task, con);
        _mto = new MoneyTransferOrder(task, con);
        String[] paramBasic = new String[]{doc, _mto.getDocNumber().trim(), _mto.getDocDateString()};
        GeneralChecker generalChecker = new GeneralChecker(con, task, paramBasic);
        String errMsg = "";

        // ������������ �� ���������� ������� �� ��
        if (_mto.getDocumentClass().equals("193")) {
            if (_mto.GetPayOrder() != null) {

                // [10250] � �� "���������� ���������� � �������" ��������� ���� ����������
                //�� ������ ����������� �� "������������ �� ���������� ������� �� �/�",
                //���� ����� ����������, �� ������� ������.
                if (_mto.getReceiverAccount().getAccountBalance() == null) {
                    errMsg += "- ���� ���������� �� ��������.\n";
                } else if (!_mto.GetPayOrder().getRecAccount().equals(_mto.getReceiverAccount().getAccountBalance().getAccNumber())) {
                    errMsg += String.format("- ��� �������� ����� \"�%s\" ��������� ���� \"�%s\" �� ������������� ���������� ����� \"�%s\", " +
                                    "���������� � �� \"���������� � ������� ����������\" �%s �� %s \n",
                            _mto.getRecAccount(), _mto.getReceiverAccount().getAccountBalance().getAccNumber(), _mto.GetPayOrder().getRecAccount(), _mto.GetPayOrder().getDocNumber(),
                            func.DateConvert(_mto.GetPayOrder().getDocDate()));
                }
            }

            Document document = new Document(task, con, _mto.getDocumentId());
            Document parentDocument = new Document(task, con, document.getParentID());

            if (parentDocument.getDocumentClassID().equals("195")) {
                if (_mto.GetAcceptOblIdAuBu() != null && _mto.GetAcceptOblId() == null && _mto.GetKesrHeadCode() != null && !_mto.GetKesrHeadCode().substring(0, 1).equals("1")) {
                    errMsg += "- ���� ��������� ������������� ������ ���� ���������.\n";
                }
            }

            //[10836] - �������� ������������ �/� ����������� ����
            generalChecker.checkMatchAccountIndustryCode(_mto.getRecAccount(), _mto.getLines(), _mto.getReceiver(), _mto.getTableName());

            //[10844] - �������� ������������ �/� � ������� ������ � �����������
            checkAcoountNumbers(_mto.getReceiverAccount(), _mto.getRecAccount(), con);

            //[10840] - �������� ���������� ���� ��������
            checkOperation(task, con, parentDocument);

            //[10846] - �������� ���������� ���� �������������
            String param = "";
            for (int i = 0; i < _mto.getLines().size(); i++) {
                generalChecker.isRefExists(_mto.getLines().get(i).getKesrCode(), _mto.getLines().get(i).getKvr(),
                        _mto.getLines().get(i).getCodeFSR(), _mto.get_accepted_odl_id(), param, _mto.getGrbsID(), _mto.getAmount(), _mto.getTableName());
            }

            if (!errMsg.equals("")) {
                emcUserException.userExp(10250, new String[]{_mto.getDocNumber().trim(), _mto.getDocDateString(), "\n" + errMsg}, _mto.getTableName());
            }
        }
    }

    private void checkAcoountNumbers(Account recAccount, String recAccountNumber, Context con) throws SQLException {
        String err = "";
        if (!recAccount.getAccNumber().equals(recAccountNumber))
            err += "������� ���� ���������� " + recAccountNumber + " � ������������ �� ������������� ���������� ����� " + recAccount.getAccNumber() + ". ������������ ������� ����.\n";
        if (!err.equals(""))
            emcUserException.userExp(10840, new String[]{doc, _mto.getDocNumber().trim(), _mto.getDocDateString(), err}, _mto.getTableName());
    }



    private void checkOperation(Element task, Context con, Document parentDoc) throws SQLException {

        String mainUFKAccount = "40701810650041080012";
        Key key = null;

        if (parentDoc.getDocumentClassID().equals("24") && !_mto.getOpertypeID().equals("0")) {
            con.throwUserException(new UserException(10840, new String[]{doc, _mto.getDocNumber().trim(), _mto.getDocDateString(),
                    _mto.getPayName(), "������� ������ ��� ��������, ������ ���� ��� �������� '0 - �� �������'"}));
        }

        if (parentDoc.getDocumentClassID().equals("195") &&
                _mto.getPayerAccount().getAccountBalance().getAccNumber().equals(mainUFKAccount) &&
                _mto.getReceiverAccount().getAccountBalance().getAccNumber().equals(mainUFKAccount)) {
            key = new Key(_mto.getOrg().getRole(), _mto.getReceiver().getRole(), _mto.getOpertypeID());
        }

        if (key == null || ListRolesAnalKind.contains(key)) return;

        String recommendOperTypeId = ListRolesAnalKind.recommendTypeOperation(key);
        OperationType recommendOperType = recommendOperTypeId == null ? null : new OperationType(task, con, recommendOperTypeId);
        String recommend = recommendOperType == null ? "������� ������ ��� ��������" : "������ ���� ������ ��� �������� '" + recommendOperType.getId() + " " + recommendOperType.getCaption() + "'";
        emcUserException.userExp(10840, new String[]{doc, _mto.getDocNumber().trim(), _mto.getDocDateString(),
                _mto.getPayName(), recommend}, _mto.getTableName());
    }

    /* ��������� ����������� �� ���.
     ���� ���Ļ � 12 �� 14 ������� ������������� ���� � ����������� ������������, ������� ��������������, ��������� 3 ����� ���� �λ*/
    private Organization getOrgWithIdentPlat(PayOrder payOrder, Context con, Element task) throws SQLException {
        if (payOrder.getPayment().getPiBudgetCode().length() != 20) return null;
        String sql = String.format("SELECT ORG_ID FROM ORGBUDGETDET WHERE CODE_FO LIKE '%s_%s'",
                payOrder.getPayment().getPiBudgetCode().substring(0, 3),
                payOrder.getPayment().getPiBudgetCode().substring(14, 17));
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        ArrayList<String> arrayOrgIDs = new ArrayList<>();
        while (rs.next()) {
            arrayOrgIDs.add(rs.getString("ORG_ID"));
        }
        return (arrayOrgIDs.size() == 1) ? new Organization(task, con, arrayOrgIDs.get(0), null) : null;
    }
}

