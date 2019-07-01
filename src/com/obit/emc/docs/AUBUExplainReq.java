package com.obit.emc.docs;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.general.emcCustomDocument;
import org.w3c.dom.Element;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by MakKN on 23.03.2017.
 */
public class AUBUExplainReq extends emcCustomDocument {

    public AUBUExplainReq(Element task, Context con) throws UserException, SQLException {
        super(task, con);
        ftask = task;
        fcon = con;
        setMainSQL("", "EXPLAINREQ", "id=?", id);
        build();
    }

    @Override
    protected void getData() throws UserException, SQLException {
        Receiver_id = mainRS.getString("RECEIVER_ID");
        Rec_account = mainRS.getString("REC_ACCOUNT");


        String sql = "select * from ORGROLES where org_id=" + mainRS.getString("RECEIVER_ID") + "and orgrole_id in (18,19)";
        ResultSet rs = execSQL(sql);
        if (rs != null) {
            Orgrole_id = rs.getString("ORGROLE_ID");
        }
    }

    public String getReceiver_id() {
        return Receiver_id;
    }

    public String getRec_account() {
        return Rec_account;
    }

    public String getOrgrole_id() {
        return Orgrole_id;
    }

    private Context fcon;
    private Element ftask;
    private String Receiver_id = "";
    private String Rec_account = "";
    private String Orgrole_id = "";
}
