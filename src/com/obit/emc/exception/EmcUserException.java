package com.obit.emc.exception;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.bssys.server.docflow.DocumentObject;
import com.bssys.server.exception.GroupUserException;
import com.bssys.server.system.auditor.DocEvErrLogObject;
import org.w3c.dom.Element;

import java.sql.SQLException;
import java.util.ArrayList;

public class EmcUserException {

    private UserException exp;
    private Context con;
    private Element task;
    private ArrayList<UserException> expList;

    public EmcUserException(Element task, Context con) {
        this.con = con;
        this.task = task;
    }

    public EmcUserException(Context con, Element task) {
        this.con = con;
        this.task = task;
        expList = new ArrayList<>();
    }

    //«аписывает ошибку в журанал ошибок
    private void writeErr(String tableName) throws SQLException {
        DocumentObject documentObject  = new DocumentObject(tableName, tableName);
        documentObject.load(this.con, Long.parseLong(this.task.getAttribute("ID")), 0);
        if (expList != null && expList.size() != 0) {
            StringBuilder error = new StringBuilder();
            for (UserException list: expList) {
                error.append(list.getMessage()).append("\n");
            }
            DocEvErrLogObject.write(this.con, documentObject, error.toString());
        }
        else {
            DocEvErrLogObject.write(this.con, documentObject, this.exp.getMessage());
        }
    }

    /**
     * ¬ыводит одиночную ошибку без параметров с указанием возможности игнорировани€
     * @param code код ошибки
     * @param ignorable возможность игнорировани€
     */
    public void userExp(int code, boolean ignorable, String tableName) throws SQLException {
        try {
            this.exp = new UserException(code);
            con.throwUserException(this.exp, ignorable);
        }
        finally {
            writeErr(tableName);
        }
    }

    public void userExp(int code, String[] param, boolean ignorable) {
        this.exp = new UserException(code, param);
        con.throwUserException(this.exp, ignorable);
    }

    public void userExp(int code, String[] param, boolean ignorable, String tableName) throws SQLException {
        try {
            this.exp = new UserException(code, param);
            con.throwUserException(this.exp, ignorable);
        }
        finally {
            writeErr(tableName);
        }
    }

    public void userExp(int code, String error, String tableName) throws SQLException {
        try {
            this.exp = new UserException(code, error);
            con.throwUserException(this.exp);
        }
        finally {
            writeErr(tableName);
        }
    }

    public void userExp(int code, String[] param, String tableName) throws SQLException {
        try {
            this.exp = new UserException(code, param);
            con.throwUserException(this.exp);
        }
        finally {
            writeErr(tableName);
        }
    }

    public void userExp(int code, String tableName) throws SQLException {
        try {
            this.exp = new UserException(code);
            con.throwUserException(this.exp);
        }
        finally {
            writeErr(tableName);
        }
    }

    public void writeExp(int code, String[] param) {
        this.expList.add(new UserException(code, param));
    }

    public void clearException() {
        this.expList.clear();
    }

    public void GroupUserExp() {
        GroupUserException groupUserException = new GroupUserException(con);
        for (UserException anExpList : expList) {
            groupUserException.addException(anExpList);
        }
        con.throwUserException(groupUserException);
    }

    public void GroupUserExp(String tableName) throws SQLException {
        if (expList.size() != 0) {
            try {
                GroupUserException groupUserException = new GroupUserException(con);
                for (UserException anExpList : expList) {
                    groupUserException.addException(anExpList);
                }
                con.throwUserException(groupUserException);
            }
            finally {
                writeErr(tableName);
            }
        }

    }

    public void userExpEDS(int code, String orgName) {
        this.exp = new UserException(code, "ќрганизаци€ " + orgName + " находитс€ на бумажном документообороте.");
        con.throwUserException(this.exp, true);
    }

}
