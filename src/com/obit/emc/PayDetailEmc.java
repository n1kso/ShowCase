package com.obit.emc;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.bssys.server.processor.CustomControl;
import com.obit.emc.checkers.EDS;
import com.obit.emc.checkers.GeneralChecker;
import com.obit.emc.docs.PayDetail;
import com.obit.emc.docs.additional.PayDetailLine;
import com.obit.emc.exception.EmcUserException;
import org.w3c.dom.Element;

import java.sql.SQLException;

public class PayDetailEmc extends CustomControl {

    private PayDetail payDetail;
    private String doc = "Cправка-уведомление об уточнении операций АУ/БУ";
    private EmcUserException emcUserException;

    public void doBeforeAction(Context con, Element task, Element result) throws UserException, SQLException {

        if (task.getAttribute("action").equals("insert") ||
                task.getAttribute("action").equals("update") ||
                task.getAttribute("action").equals("delete") ||
                task.getAttribute("action").equals("create") ||
                task.getAttribute("action").equals("decline") ||
                task.getAttribute("action").equals("agreement") ||
                task.getAttribute("action").equals("cbank_insert")) return;

        payDetail = new PayDetail(task, con);
        emcUserException = new EmcUserException( task, con);

        String[] paramBasic = new String[]{doc, payDetail.getDocNumber().trim(), payDetail.getDocDateString()};
        GeneralChecker generalChecker = new GeneralChecker(con, task, paramBasic);

        if (task.getAttribute("action").equals("process")) {
            // [10833] – Контроль соответствия л/с отраслевому коду
            generalChecker.checkMatchAccountIndustryCode(payDetail, payDetail.getOrg(), payDetail.getTableName());

            // [10844] - Конртоль соответствия счетов в уточняемых и уточннных строка с счетами в справочнике
            checkAccountNumbers(payDetail);

            // [10842] - Проверка выбранного типа операции в ЭД "Справка-уведомление об уточнении операций БУ/АУ"
            checkOperType(payDetail);

            // [10849] - Проверка выбранного типа операции в ЭД "Справка-уведомление об уточнении операций БУ/АУ"
            checkDirectionInLines(payDetail);

            // [10846] - Проверка заполнения поля Обязательство
            String param = " в уточненной строке";
            for (int i = 0; i < payDetail.getDstLines().size(); i++) {
                generalChecker.isRefExists(payDetail.getDstLines().get(i).getKesrCode(), payDetail.getDstLines().get(i).getKvr(),
                        payDetail.getDstLines().get(i).getFSR_ID(), payDetail.getDstLines().get(i).getConBudgId(), param, payDetail.getOrgGrbsID(), payDetail.getAmount(), payDetail.getTableName());
            }
        }

        if (task.getAttribute("action").equals("agreement") && payDetail.getDispStatus().equals("1")) {
            EDS eds = new EDS(payDetail.getDocumentId(), payDetail.getPayID(), con, task, doc, payDetail.getDocNumber().trim(), payDetail.getDocDateString());
            eds.checkEDS(payDetail.getTableName());
        }
    }

    private void checkOperType(PayDetail payDetail) throws SQLException {
        String errMsg = "";
        boolean isKVFOequals = payDetail.getSourceLines().get(0).getFSR_ID().equals(payDetail.getDstLines().get(0).getFSR_ID());

        if (!isKVFOequals && !payDetail.getOperTypeID().equals("-28")) {
            errMsg = " Тип операции должен быть '-28 Уточнение через счет учета средств по КО'";
        }
        else if (isKVFOequals && (payDetail.getPayOrg().getRole().equals(payDetail.getRecOrg().getRole()) ||
                payDetail.getPayOrg().getRole().equals("4") && payDetail.getRecOrg().getRole().equals("18")) && !payDetail.getOperTypeID().equals("0")) {
            errMsg = " Тип операции должен быть '0 - НЕ УКАЗАНА'";
        }
        else if ((payDetail.getPayOrg().getRole().equals("4") && payDetail.getRecOrg().getRole().equals("19") ||
                payDetail.getPayOrg().getRole().equals("18") && payDetail.getRecOrg().getRole().equals("19") ||
                payDetail.getPayOrg().getRole().equals("19") && payDetail.getRecOrg().getRole().equals("18")) &&
                !payDetail.getOperTypeID().equals("-28")) {
            errMsg = " Тип операции должен быть '-28 Уточнение через счет учета средств по КО'";
        }
        if (!errMsg.equals("")) {
            emcUserException.userExp(10840, new String[]{doc, payDetail.getDocNumber().trim(), payDetail.getDocDateString(),
                    payDetail.getPayOrg().getShortName(), errMsg}, payDetail.getTableName());
        }
    }

    private void checkAccountNumbers(PayDetail payDetail) throws SQLException {

        if (payDetail.getSourceLines() != null && payDetail.getDstLines() != null) {
            StringBuilder err = new StringBuilder();
            for (PayDetailLine line : payDetail.getSourceLines()) {
                if (!line.getAccount().getAccNumber().equals(line.getRecAccount())) {
                    err.append("счет ").append(line.getRecAccount()).append(" в уточняемой строке не соответствует выбранному счету ")
                            .append(line.getAccount().getAccNumber()).append(". Перевыберите счет в уточняемой строке.\n");
                    break;
                }
            }
            for (PayDetailLine line : payDetail.getDstLines()) {
                if (!line.getAccount().getAccNumber().equals(line.getRecAccount())) {
                    err.append("счет ").append(line.getRecAccount()).append(" в уточненной строке не соответствует выбранному счету ")
                            .append(line.getAccount().getAccNumber()).append(". Перевыберите счет в уточненной строке.\n");
                    break;
                }
            }
            if (!err.toString().equals("")) {
                    emcUserException.userExp(10844, new String[]{doc, this.payDetail.getDocNumber().trim(),
                            this.payDetail.getDocDateString(), this.payDetail.getPayOrg().getShortName(), err.toString()}, this.payDetail.getTableName());
            }
        }
    }

    private void checkDirectionInLines(PayDetail payDetail) throws SQLException {

        StringBuilder err = new StringBuilder();

        for (PayDetailLine line: payDetail.getSourceLines()) {
            if (line.getDirection().equals(""))
                err.append("В уточняемой строке необходимо выбрать значение в поле \"Направление операции\"\n");
        }

        for (PayDetailLine line: payDetail.getDstLines()) {
            if (line.getDirection().equals(""))
                err.append("В уточненной строке необходимо выбрать значение в поле \"Направление операции\"\n");
        }

        if (!err.toString().equals("")) {
                emcUserException.userExp(10849, new String[]{doc, this.payDetail.getDocNumber().trim(),
                        this.payDetail.getDocDateString(), this.payDetail.getPayOrg().getShortName(), err.toString()}, this.payDetail.getTableName());
        }
    }
}
