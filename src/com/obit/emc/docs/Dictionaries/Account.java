/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.obit.emc.docs.Dictionaries;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.general.emcCustomDic;
import java.sql.SQLException;
import java.time.LocalDate;

import org.w3c.dom.Element;

/**
 *
 * @author KanMA
 */
public class Account extends emcCustomDic{
   
  public Account(String id, String AccNumber) {
    super(id);
    this.AccNumber = AccNumber;
  }
  /**Конструктор для создания экземпляра класса из БД
   * @param task  xml представление задания СП
   * @param con контекст запросов к БД
   * @param  id идентификатор счета
   * @throw UserException в случае если не передан id 
   * @throw SQLException
   */
  public Account(Element task, Context con, String id) throws  UserException, SQLException {
    super(task,con,id);
    if (id.equals("")) {
     return;
    }
    setMainSQL("","orgaccount","id=?",id);
    build();
  }


  /**@return номер счета*/
  public String getAccNumber() {
    return AccNumber;
  }


  
  protected void getData() throws UserException, SQLException {
    AccNumber = mainRS.getString("ACCOUNT_NUMBER");
    openDate = mainRS.getTimestamp("OPEN_DATE") != null ?  mainRS.getTimestamp("OPEN_DATE").toLocalDateTime().toLocalDate() : null;
    closeDate =  mainRS.getTimestamp("CLOSE_DATE") != null ? mainRS.getTimestamp("CLOSE_DATE").toLocalDateTime().toLocalDate() : null;
    _keeperAccID = mainRS.getString("KEEPERACC_ID");
      _orgacctypeid = mainRS.getString("ORGACCTYPE_ID");

    if (mainRS.getString("ORGACCTYPE_ID").equals("1"))
    {
       accountBalance = new AccountBalance(fTask,fcon,_keeperAccID);
    }
  }

  private String AccNumber = "";
  private String _orgacctypeid;
  private LocalDate openDate;
  private LocalDate closeDate;
  private String _keeperAccID = "";
  private AccountBalance accountBalance = null;


    public String getKeeperAccID() {
        if (_keeperAccID == null) return "";
        return _keeperAccID;
    }

    public AccountBalance getAccountBalance() {
        return accountBalance;
    }

    public String getOrgAccType_id() {
        return _orgacctypeid;
    }

    public LocalDate getCloseDate() {
        return closeDate;
    }
}
