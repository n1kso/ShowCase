package com.obit.emc.docs.additional;

/**
 * Created by MakKN on 22.05.2017.
 */
public class RrLine {
    public RrLine(String _PURPOSEFULGRANT_ID) {
        PURPOSEFULGRANT_ID = _PURPOSEFULGRANT_ID;
    }

    private String PURPOSEFULGRANT_ID = "";

    public String getPURPOSEFULGRANT_ID() {
        if (PURPOSEFULGRANT_ID == null || PURPOSEFULGRANT_ID.equals(""))
            return "";
        return PURPOSEFULGRANT_ID;
    }
}
