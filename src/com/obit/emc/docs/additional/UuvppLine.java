package com.obit.emc.docs.additional;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import org.w3c.dom.Element;

import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: CherIA
 * Date: 17.05.13
 * Time: 12:29
 * To change this template use File | Settings | File Templates.
 */
public class UuvppLine {

    public UuvppLine(String _KFSR_CODE, String _KCSR_CODE, String _KVR_CODE, String _KESR_CODE,
                     String _KADMR_CODE, String _KDF_CODE, String _KDE_CODE, String _KDR_CODE) throws UserException, SQLException {
        KFSR_CODE = _KFSR_CODE;
        KCSR_CODE = _KCSR_CODE;
        KVR_CODE = _KVR_CODE;
        KESR_CODE = _KESR_CODE;
        KADMR_CODE = _KADMR_CODE;
        KDF_CODE = _KDF_CODE;
        KDE_CODE = _KDE_CODE;
        KDR_CODE = _KDR_CODE;
    }

    private String KFSR_CODE = "";
    private String KCSR_CODE = "";
    private String KVR_CODE = "";
    private String KESR_CODE = "";
    private String KADMR_CODE = "";
    private String KDF_CODE = "";
    private String KDE_CODE = "";
    private String KDR_CODE = "";

    public String getKFSR_CODE() {
        if (KFSR_CODE == null || KFSR_CODE.equals("")) return "";
        else
            return KFSR_CODE.trim();
    }

    public String getKCSR_CODE() {
        if (KCSR_CODE == null || KCSR_CODE.equals("")) return "";
        else
            return KCSR_CODE.trim();
    }


    public String getKVR_CODE() {
        if (KVR_CODE == null || KVR_CODE.equals("")) return "";
        else
            return KVR_CODE.trim();
    }


    public String getKESR_CODE() {
        if (KESR_CODE == null || KESR_CODE.equals("")) return "";
        else
            return KESR_CODE.trim();
    }


    public String getKADMR_CODE() {
        if (KADMR_CODE == null || KADMR_CODE.equals("")) return "";
        else
            return KADMR_CODE.trim();
    }


    public String getKDF_CODE() {
        if (KDF_CODE == null || KDF_CODE.equals("")) return "";
        else
            return KDF_CODE.trim();
    }


    public String getKDE_CODE() {
        if (KDE_CODE == null || KDE_CODE.equals("")) return "";
        else
            return KDE_CODE.trim();
    }


    public String getKDR_CODE() {
        if (KDR_CODE == null || KDR_CODE.equals("")) return "";
        else
            return KDR_CODE.trim();
    }
}
