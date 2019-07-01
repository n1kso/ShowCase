package com.obit.emc;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.bssys.server.processor.CustomControl;
import com.obit.emc.docs.Dictionaries.ExpBudget;
import com.obit.emc.docs.Dictionaries.SysUser;
import com.obit.emc.docs.Ubn;
import com.obit.emc.docs.UobnOnlyLines;
import org.w3c.dom.Element;

import java.sql.SQLException;
/**
 * Created by IntelliJ IDEA.
 * User: KanMA
 * Date: 30.01.2012
 * Time: 9:46:26
 * To change this template use File | Settings | File Templates.
 */
public class UobnEmc extends CustomControl {

    public void doBeforeAction(Context con, Element task, Element result) throws UserException, SQLException
    {
        if(task.getAttribute("action").equals("finish_process")  || task.getAttribute("action").equals("cancel")
                || task.getAttribute("action").equals("return"))
        {
            // игнорируем контроль для КФ
            SysUser usr = new SysUser(task,con);
            String orgid = usr.getOrgID();
            String username = usr.getUserName();

            if (!username.equals("root"))
            {
                Ubn ubn = new Ubn(task, con);
                for (int i = 0; i<ubn.GetLines().size();i++)
                {
                    if(!ubn.GetLines().get(i).get_assign_amt1().equals("0") || !ubn.GetLines().get(i).get_assign_amt2().equals("0") ||
                            !ubn.GetLines().get(i).get_assign_amt3().equals("0"))
                    {
                        con.throwUserException(new UserException(10128," Изменение ассигнований производится в системе \"АЦК-Планирование\""));
                    }
                    //№1186
                    if ( (Double.parseDouble(ubn.GetLines().get(i).get_limit_amt1())<0 || Double.parseDouble(ubn.GetLines().get(i).get_limit_amt2())<0 ||
                            Double.parseDouble(ubn.GetLines().get(i).get_limit_amt3())<0) && (ubn.GetLines().get(i).getKbk().getKDR_CODE().equals("900") || ubn.GetLines().get(i).getKbk().getKDR_CODE().equals("600")
                            || ubn.GetLines().get(i).getKbk().getKDR_CODE().equals("060")))
                    {
                        con.throwUserException(new UserException(10130," У Вас нет доступа на изменение лимитов по Доп.КР 900, Доп.КР 600, Доп. КР 060"));
                    }
                }
            }

            if (orgid.equals("1000000001")) return;
            UobnOnlyLines uobn = new UobnOnlyLines(task,con);
            if(uobn.is_hasRospisLine())
            {
                int code  = task.getAttribute("documentclass_id").equals("127")?10127:10128;
                con.throwUserException(new UserException(code," У Вас нет прав на обработку бюджетных назначений по распорядительным строкам"));
            }
        }
        if (task.getAttribute("action").equals("finish_process") || task.getAttribute("action").equals("process"))
        {
            StringBuilder err = new StringBuilder();
            Ubn ob = new Ubn(task,con);
            int code2  = task.getAttribute("documentclass_id").equals("127")?10127:10128;
            ExpBudget eb = new ExpBudget();
            for (int i = 0; i<ob.GetLines().size();i++)
            {
                if(ob.GetLines().get(i).IsHead()) continue;
                if(ob.GetLines().get(i).GetKif().equals("9") && (!ob.GetLines().get(i).GetMemGroupCode().equals("") &&
                        !ob.GetLines().get(i).GetMemGroupCode().equals("0")))
                {
                    if(!eb.CheckExpBudget(task,con,ob.GetLines().get(i)))
                        err.append(ob.GetLines().get(i).getKbk().ToStr()).append("\n");
                }
            }
            if (!err.toString().equals(""))
                con.throwUserException(new UserException(code2," Нельзя использовать группу по бухгалтерии в документе по целевым субсидиям в строках:\n"+err));
        }
    }
}
