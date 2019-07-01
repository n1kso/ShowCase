package com.obit.emc.docs.Dictionaries;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.general.emcCustomDic;
import java.sql.SQLException;
import org.w3c.dom.Element;

/**
 * @module com.obit.emc.docs.Dictionaries.BudgetObligation
 * Бюджетное обязательство 
 * @author KanMA
 */
public class BudgetObligation extends emcCustomDic {


  public BudgetObligation(Element task, Context con, String id) throws UserException, SQLException {
    super(task, con, id);
    setMainSQL("","budgorder","id=?",id);
    build();
  }

  
  protected void getData() throws UserException, SQLException {
   documentId = mainRS.getString("DOCUMENT_ID");
  }

  private String documentId="";

  public String getDocumentId() {return documentId;}
  

}
