package com.obit.emc.docs.Dictionaries;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.general.emcCustomDic;

import java.math.BigDecimal;
import java.sql.SQLException;
import org.w3c.dom.Element;


/**
 *  @module com.obit.emc.docs.Dictionaries.Contract;
 * Денежное обязательство (Контракт)
 * @author KanMA
 */
public class Contract extends emcCustomDic {

  public Contract(Element task, Context con, String id) throws UserException, SQLException {
    super(task, con, id);
    setMainSQL("","contract","id=?",id);
    build();
  }

  public Contract(String name,String tax,String kpp,String id) {
    super(id);
    recKPP = kpp;
    recName = name;
    recTax = tax;
  }
  
  public String getRecName() {
    return recName;
  }

  public String getRecTax() {
    return recTax;
  }

  public String getRecKPP() {
    return recKPP;
  }

  public BigDecimal getContractAmount() {return contractAmount;}
  public BigDecimal getLastYearAmt() {return lastYearAmt;}
  public BigDecimal getFullAmount() {return fullAmount;}
  public BigDecimal getRsvAmount() {return rsvAmount;}
  public String getDocumentId() {return documentId;}
  public BigDecimal getAcc_obl_contract_amount() {return acc_obl_contract_amount; }

  @Override
  protected void getData() throws UserException, SQLException {
    recName = mainRS.getString("REC_NAME");
    recTax = mainRS.getString("REC_TAXCODE");
    recKPP = mainRS.getString("REC_KPP");
    contractAmount = mainRS.getBigDecimal("CONTRACT_AMOUNT");
    acc_obl_contract_amount = mainRS.getBigDecimal("ACC_OBL_CONTRACT_AMOUNT");
    lastYearAmt = mainRS.getBigDecimal("LAST_YEAR_AMT");
    fullAmount = mainRS.getBigDecimal("FUL_AMOUNT");
    rsvAmount = mainRS.getBigDecimal("RSV_AMOUNT");
    documentId = mainRS.getString("DOCUMENT_ID");
  }

  private String recName = "";
  private String recTax = "";
  private String recKPP = "";
  private BigDecimal contractAmount; // Общая сумма
  private BigDecimal lastYearAmt; // Исп. на нач. года (Договор) Исп. в прошлых периодах (Сведение об обяз. БУ/АУ)
  private BigDecimal fullAmount; // Сумма исполнено (Договор / Заявка БУ/АУ)
  private BigDecimal rsvAmount; // Сумма в процессе исполнения
  private String documentId="";
  private BigDecimal acc_obl_contract_amount;

}
