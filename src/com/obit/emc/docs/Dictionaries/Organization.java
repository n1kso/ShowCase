/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.obit.emc.docs.Dictionaries;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.func.func;
import com.obit.emc.general.emcCustomDic;
import org.w3c.dom.Element;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author KanMA
 */
public class Organization extends emcCustomDic{

    private String orgID; // ID организации
    private String closeDate; // Дата закрытия организации
    private ArrayList<String> roles; // Роли организации
    private ArrayList<String> accountNumbersFO; // Счета УФК организации
    private String personage; // Признак: Физ.лицо/ Юр.лицо/ Нерезидент

  public Organization(Element task, Context con, String id,String AccID) throws UserException, SQLException {
    super(task,con,id);
    this.accId = AccID == null ? "" : AccID;
    setMainSQL("","org","id=?",id);
    build();
  }

  public Organization(Element task, Context con, String id) throws UserException, SQLException {
    super(task,con,id);
    setMainSQL("","org","id=?",id);
    build();
  }

  
  public Organization(Context con,String aId, String aName, String aKpp, String aTax,String AccID) throws UserException, SQLException {
    super(aId);
    fcon = con;
    this.orgName = aName;
    this.orgKpp = aKpp;
    this.orgTax = aTax;
    this.accId = AccID == null ? "" : AccID;
    buildAddData();
  }
  
  public String getOrgName() {
    if (orgName == null) return "";
    return orgName;
  }

  public String getOrgKpp() {
    if (orgKpp == null) return "";
    return orgKpp;
  }

  public String getOrgTax() {
    if (orgTax == null) return "";
    return orgTax;
  }

  public Account getAcc() {
    return acc;
  }
  
  public String getID(){
    if (this.id == null) return "";
    return this.id;
  }

  
  protected void getData() throws UserException, SQLException {
    orgName = mainRS.getString("DESCRIPTION");
    ShortName =  mainRS.getString("CAPTION");
    orgTax = mainRS.getString("taxcode");
    orgKpp = mainRS.getString("kpp");
    orgID = mainRS.getString("ID");
    closeDate = mainRS.getString("close_date");
    personage = mainRS.getString("personage");
    roles = loadRoles();
    accountNumbersFO = loadAccountNumbersFO();
    buildAddData();
  }
  
  /**заполняем дополнительные поля*/
  private void buildAddData()throws UserException,SQLException{
    acc = new Account(fTask, fcon,accId);
  }
  /** наименование организации*/
  private String orgName = "";
  /**КПП организации*/
  private String orgKpp = "";
  /**ИНН организации*/
  private String orgTax = "";
  /**ИД счета*/
  private String accId = "";

  private String ShortName ="";   
  private Account acc = null;

    public String getRole(){
      if (roles.contains("18")) {
        return "18";
      }
      if (roles.contains("19")) {
        return "19";
      }
      if (roles.contains("22")) {
        return "22";
      }
      if (roles.contains("4")) {
        return "4";
      }
      return "";
    }

    public String getShortName() {
        if (ShortName == null) return "";
        return ShortName;
    }

    public String getCloseDate() {
      if (closeDate != null) return func.DateConvert(closeDate);
      return null;
    }

  public ArrayList<String> getRoles() {
    return roles;
  }

  public ArrayList<String> getAccountNumbersFO() {
    return accountNumbersFO;
  }

  public String getPersonage() {
    return personage == null ? "" : personage;
  }

  private ArrayList<String> loadRoles() throws SQLException {
      ArrayList<String> roles = new ArrayList<>();
      String sql = "SELECT * from ORGROLES o\n" +
              "WHERE o.ORG_ID=" + orgID;
      PreparedStatement ps = fcon.prepareStatement(sql);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        roles.add(rs.getString("ORGROLE_ID"));
      }
      return roles;
    }

  private ArrayList<String> loadAccountNumbersFO() throws SQLException {
    ArrayList<String> numbers = new ArrayList<>();
    String sql = "SELECT DISTINCT KEEPERACC.ACCOUNT_NUMBER FROM ORGACCOUNT ORGACC\n" +
            "  JOIN ORGACCOUNT KEEPERACC ON ORGACC.KEEPERACC_ID = KEEPERACC.ID\n" +
            "WHERE ORGACC.ORGACCTYPE_ID=1 AND (ORGACC.CLOSE_DATE IS NULL OR ORGACC.CLOSE_DATE>CURRENT_DATE) and ORGACC.ORG_ID=" + orgID;
    PreparedStatement ps = fcon.prepareStatement(sql);
    ResultSet rs = ps.executeQuery();
    while (rs.next()) {
      numbers.add(rs.getString("ACCOUNT_NUMBER"));
    }
    return numbers;
  }

    /**
     * Проверка актуальности организации
     * @return false если организация не значится актуальной
     * @throws SQLException Исключение
     */
    public boolean isActualOrg() throws SQLException {
        String sql = "SELECT * FROM CONTRACTOR WHERE ORG_ID=" + orgID;
        PreparedStatement ps = fcon.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        return rs.next();
    }
}
