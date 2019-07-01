package com.obit.emc.docs;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.docs.Dictionaries.Document;
import com.obit.emc.general.emcCustomDocument;
import org.w3c.dom.Element;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by ChesDV on 06.07.2017.
 */
public class Rzsi extends emcCustomDocument {

    private final String tableName = "SRCORDER";

    public Rzsi(Element task, Context con) throws UserException, SQLException {
        super(task, con);
        setMainSQL("select * from SRCORDER where id = " + id);
        build();
    }


    @Override
    protected void getData() throws UserException, SQLException {
        oper_type = mainRS.getString("Opertype_Id");
        kvi = mainRS.getString("Ki_Code");
        Document Doc = new Document(fTask, fcon, getDocumentId());

       /* String sql = "SELECT * FROM document where id="+ Doc.getParentID();
        ResultSet rs = execSQL(sql);
        if (rs!=null)
        {
            parent_id= rs.getString("id");
        }*/

        String sql1 = "Select * from PAYORDER where document_id = " + Doc.getParentID();
        ResultSet rs1 = execSQL(sql1);
        if (rs1 != null) {
            pay_acc = rs1.getString("Pay_Account");
        }
    }

    private String oper_type = "";
    private String kvi = "";
    private String pay_acc = "";

    public String Getoper_type() {
        return oper_type;
    }

    public String Getkvi() {
        return kvi;
    }

    public String Getpay_acc() {
        return pay_acc;
    }

    public String getTableName() {
        return tableName;
    }
}
