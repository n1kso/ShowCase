/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.obit.emc.general;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import java.sql.SQLException;
import  org.w3c.dom.Element;
/*import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator; */
  
/**
 *
 * @author KanMA
 */
abstract public class emcCustomDic extends emcCustomRoot {
 // получаем данные из БД запрососм
  public emcCustomDic(Element task, Context con, String id ) {
 //   super(task,con);
    this.id = id;
    this.fTask = task;
    this.fcon = con;
  }

  // заполняем руками
  public emcCustomDic(String id) {
    this.id=id; 
  }
  

  protected void build() throws SQLException, UserException {
        activate();
        getData();
        deactivate();
    }

    abstract protected void getData() throws UserException, SQLException; 
}
