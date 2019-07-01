package com.obit.emc;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.bssys.server.processor.CustomControl;
import com.obit.emc.docs.AUBUContractInformation;
import com.obit.emc.exception.EmcUserException;
import org.w3c.dom.Element;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AUBUContractInformationEmc extends CustomControl {

    public void doBeforeAction(Context con, Element task, Element result) throws UserException, SQLException {



        String doc = "Сведение об обязательствах и договоре БУ/АУ";
        String err = "";

        if (!task.getAttribute("action").equals("prepare")) return;


        AUBUContractInformation aubuContractInformation = new AUBUContractInformation(task, con);

        String sql = "SELECT * FROM docjournal d " +
                "INNER JOIN taskjournal t ON t.id = d.taskjournal_id " +
                "WHERE document_id = " + aubuContractInformation.getDocumentId() + " ORDER BY WORK_DATE ASC";
        PreparedStatement ps = con.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            if (rs.getString("SYSUSER_ID").equals("1000002258"))
                return;
        }
        if (aubuContractInformation.getOrg().getRoles().contains("22") && !aubuContractInformation.getContractType().equals("1000000046")) {
            err += "Вид договора должен быть 'Контракт МП' \n";
        }
        else if (!aubuContractInformation.getContractType().equals("1000000006") && !aubuContractInformation.getOrg().getRoles().contains("22"))
            err += "Вид договора должен быть 'Договор в рамках 223-ФЗ' \n";

        if (!err.equals("")) {
            new EmcUserException(task, con).userExp(10847, new String[]{doc, aubuContractInformation.getDocNumber().trim(),
                    aubuContractInformation.getDocDateString(), err}, aubuContractInformation.getTableName());
        }

    }

}
