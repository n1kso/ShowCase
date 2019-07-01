/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.obit.emc.general;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import java.sql.SQLException;
import  org.w3c.dom.Element;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author KanMA
 */
public class emcCustomRoot {
  
  protected void setMainSQL(String sql) throws SQLException {
        mainSql = sql;
        ps = fcon.prepareStatement(mainSql);
    }

    /**
     * метод собирает SQL запрос получающий данные документа (с одним строковым параметром)
     *
     * @param getFields  поля которые необходимо вернуть, если пусто то заменяется на *
     * @param table      имя таблицы базы данных, если пусто то создается исключение
     * @param whereField выражение where задается формате id=?  если пусто то запрос выполнится без выражения where
     * @param value      значение  параметра
     * @throws UserException в случае если не указана таблица
     */
    protected void setMainSQL(String getFields, String table, String whereField, String value) throws UserException, SQLException {
        if (fcon==null) throw new UnsupportedOperationException("В объекте с ручным созданием данный механизм не поддерживается");
        // проверка на непустоту
        if (getFields == null || getFields.equals("")) getFields = "*";
        if (table == null || table.equals(""))
            new UserException("setMainSQL: не передано значение table ");
                    //fTask.getAttribute("DOCUMENTCLASS_ID"));
        if (whereField == null || value == null) {
            whereField = "";
            value = "";
        }
        mainSql = "SELECT " + getFields + " FROM " + table;
        if (!whereField.equals("")) mainSql = mainSql + " WHERE " + whereField;
        ps = fcon.prepareStatement(mainSql);
    //    throw new SQLException(mainSql);
        if (!value.equals("")) ps.setString(1, value);
    }

    
    /**
     * метод собирает SQL запрос получающий данные документа (с одним строковым параметром)
     *
     * @param getFields  поля которые необходимо вернуть, если пусто то заменяется на *
     * @param table      имя таблицы базы данных, если пусто то создается исключение
     * @param whereField выражение where задается формате id=?  если пусто то запрос выполнится без выражения where
     * @param values      очередь параметров
     * @throws UserException в случае если не указана таблица
     */
    protected void setMainSQL(String getFields, String table, String whereField, ArrayList<String> values) throws UserException, SQLException {
        if (fcon==null) throw new UnsupportedOperationException("В объекте с ручным созданием данный механизм не поддерживается");
        // проверка на непустоту      
        if (getFields == null || getFields.equals("")) getFields = "*";
        if (table == null || table.equals(""))
            new UserException("setMainSQL: не передано значение table в классе документа ");
                    
        if (whereField == null || values == null) {
            whereField = "";            
        }
        mainSql = "SELECT " + getFields + " FROM " + table;
        if (!whereField.equals("")) mainSql = mainSql + " WHERE " + whereField;
        ps = fcon.prepareStatement(mainSql);
       
        // заполняем параметры запроса
        Iterator<String> itr=values.iterator();
        int i =0;
        while (itr.hasNext()){
          ps.setString(++i, itr.next());
        }
    }

    
    
        /**
     * метод собирает SQL запрос получающий данные документа 
     *
     * @param getFields  поля которые необходимо вернуть, если пусто то заменяется на *
     * @param table      имя таблицы базы данных, если пусто то создается исключение
     * @param joins      выражение join с описаниями Алиасов
     * @param whereField выражение where задается формате id=?  если пусто то запрос выполнится без выражения where
     * --@param value      очередь параметров
     * @throws UserException в случае если не указана таблица
     */
    protected void setMainSQL(String getFields, String table,String joins, String whereField, ArrayList<String> values) throws UserException, SQLException {
        if (fcon==null) throw new UnsupportedOperationException("В объекте с ручным созданием данный механизм не поддерживается");
      // проверка на непустоту
        if (getFields == null || getFields.equals("")) getFields = "*";
        if (table == null || table.equals(""))
            new UserException("setMainSQL: не передано значение table в классе документа " );
                   
        if (whereField == null || values == null) {
            whereField = "";            
        }
        if (joins==null) joins = "";
        mainSql = "SELECT " + getFields + " FROM " + table + joins;
        if (!whereField.equals("")) mainSql = mainSql + " WHERE " + whereField;
        ps = fcon.prepareStatement(mainSql);
       
        // заполняем параметры запроса
        Iterator<String> itr=values.iterator();
        int i =0;
        while (itr.hasNext()){
          ps.setString(++i, itr.next());
        }
    }

    protected ResultSet execSQL(String sql) throws SQLException {
        PreparedStatement ps = fcon.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        if (!rs.next()) rs = null;
        return rs;
    }

    protected ResultSet executeSQL(String sql) throws SQLException {
        PreparedStatement ps = fcon.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        return rs;
    }

    protected void activate() throws SQLException {
        if (ps != null) {
            mainRS = ps.executeQuery();
            if (!mainRS.next()) mainRS = null;
        }
    }

    protected void deactivate() throws SQLException {
      if (ps!=null) ps.close();
      if (mainRS != null) mainRS.close();
        
    }

    protected String id = "";
    
    protected Element fTask = null;
    protected Context fcon = null;

    protected ResultSet mainRS = null;

    private String mainSql = "";
    private PreparedStatement ps = null;

}
