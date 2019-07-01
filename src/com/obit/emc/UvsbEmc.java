package com.obit.emc;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.bssys.server.processor.CustomControl;
import com.obit.emc.checkers.UvsbChecker;
import com.obit.emc.docs.Dictionaries.Document;
import com.obit.emc.docs.Dictionaries.Estimate;
import com.obit.emc.docs.Uvsb;
import com.obit.emc.exception.EmcUserException;
import org.w3c.dom.Element;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: CherIA
 * Date: 27.06.13
 * Time: 9:16
 * To change this template use File | Settings | File Templates.
 */
public class UvsbEmc extends CustomControl {
    public void doBeforeAction(Context con, Element task, Element result) throws UserException, SQLException {
        String err = "";
        String act = task.getAttribute("action");

        if (!act.equals("finalize") && !act.equals("process")) return;

        Uvsb _uvsb = new Uvsb(task, con);
        Document document = new Document(task, con, _uvsb.get_documentID());
        Document parentDocument = new Document(task, con, document.getParentID());
        Estimate estimate = new Estimate(task, con, _uvsb.get_estimateID());
        // Задача №1303
        if (parentDocument.getDocumentClassID().equals("6") && parentDocument.getOpertypeID().equals("-1") && estimate.GetOrgId().equals("1000000001")
                && !_uvsb.get_operTypeID().equals("42"))
            err += "* Неверно указан тип операции. Необходимо указать тип операции \"42 Возврат неклассифицированных поступлений КФ\" \n";
        if (parentDocument.getDocumentClassID().equals("6") && parentDocument.getOpertypeID().equals("-1") && _uvsb.get_operTypeID().equals("42")
                && !estimate.GetOrgId().equals("1000000001")) err += "* Неверно указан тип операции \n";
        if (parentDocument.getDocumentClassID().equals("6") && parentDocument.getOpertypeID().equals("-1") && _uvsb.get_operTypeID().equals("0")
                && !estimate.GetOrgId().equals("1000000001")) err += "* Неверно указан тип операции \n";
        // Задача №957
        if (!UvsbChecker.Check_KADMR(_uvsb.get_permission_id(), _uvsb.getKbk().getKADMR_CODE()))
            err += "* Не заполнен КВСР \n";
        if (!err.equals("")) {
            String DocDate = _uvsb.get_docdate();
            new EmcUserException(task, con).userExp(10200, "\nОбнаружены ошибки в документе № " +
                    _uvsb.get_docNumber().trim() + " за " + DocDate + ":\n" + err, _uvsb.getTableName());
        }
    }
}