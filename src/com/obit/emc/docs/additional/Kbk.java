package com.obit.emc.docs.additional;

/**
 * Коды бюджетной классификации
 *
 * @author KanMA
 */
public class Kbk {

    public Kbk(String KFSR_CODE, String KCSR_CODE, String KVR_CODE, String KESR_CODE,
               String KADMR_CODE, String KDF_CODE, String KDE_CODE, String KDR_CODE,
               String FSR_ID) {
        this.KFSR_CODE = KFSR_CODE;
        this.KCSR_CODE = KCSR_CODE;
        this.KVR_CODE = KVR_CODE;
        this.KESR_CODE = KESR_CODE;
        this.KADMR_CODE = KADMR_CODE;
        this.KDF_CODE = KDF_CODE;
        this.KDE_CODE = KDE_CODE;
        this.KDR_CODE = KDR_CODE;
        this.FSR_ID = FSR_ID;
    }


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


    public String getFSR_ID() {
        if (FSR_ID == null || FSR_ID.equals("")) return "";
        else
            return FSR_ID.trim();
    }

    public String ToStr() {
        return " КФСР = " + getKFSR_CODE() +
                " КЦСР = " + getKCSR_CODE() +
                " КВР = " + getKVR_CODE() +
                " КОСГУ = " + getKESR_CODE() +
                " КВСР = " + getKADMR_CODE() +
                " Доп ФК = " + getKDF_CODE() +
                " Доп ЭК = " + getKDE_CODE() +
                " Доп КР = " + getKDR_CODE();
    }


    /**
     * КФСР
     */
    private String KFSR_CODE = "";
    /**
     * КЦСР
     */
    private String KCSR_CODE = "";
    /**
     * КВР
     */
    private String KVR_CODE = "";
    /**
     * КЭС
     */
    private String KESR_CODE = "";
    /**
     * КВСР
     */
    private String KADMR_CODE = "";
    /**
     * Доп ФК
     */
    private String KDF_CODE = "";
    /*Доп ЭК*/
    private String KDE_CODE = "";
    /**
     * Доп КР
     */
    private String KDR_CODE = "";
    /**
     * ИФ
     */
    private String FSR_ID = "";


}
