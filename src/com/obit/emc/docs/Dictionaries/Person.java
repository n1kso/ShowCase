package com.obit.emc.docs.Dictionaries;

import com.bssys.server.Context;
import com.bssys.server.UserException;
import com.obit.emc.general.emcCustomDocument;
import org.w3c.dom.Element;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

public class Person extends emcCustomDocument {

    private ArrayList<String> appointmentID = new ArrayList<>();
    private ArrayList<String> FIO = new ArrayList<>();
    private ArrayList<String> actualFlag = new ArrayList<>();

    private ArrayList<String> resppersonBuh = new ArrayList<>(Arrays.asList("19", "-3", "33", "21", "35", "40", "48",
            "15", "14", "70", "71", "75", "61", "91", "94", "96", "97"));
    private ArrayList<String> resppersonHead = new ArrayList<>(Arrays.asList("-2", "1", "3", "4", "7", "8", "22", "23",
            "24", "34", "36", "37", "38", "39", "41", "43", "44", "45", "46", "26", "27", "28", "29", "30", "33", "49",
            "50", "51", "52", "53", "54", "55", "56", "57", "60", "62", "63", "64", "65", "66", "69", "78", "84", "88",
            "90", "95", "99", "100"));

    public Person(Element task, Context con, String orgID) throws UserException, SQLException {
        super(task, con);
        fcon = con;
        fTask = task;
        setMainSQL("", "respperson", "org_id=?", orgID);
        buildNotDocument();
    }

    @Override
    protected void getData() throws UserException, SQLException {
        do {
            appointmentID.add(mainRS.getString("APPOINTMENT_ID"));
            FIO.add(mainRS.getString("FIO"));
            actualFlag.add(mainRS.getString("ACTUAL_FLAG"));
        } while (mainRS.next());
    }

    public ArrayList<String> getListHead(String actual) {
        ArrayList<String> temp = new ArrayList<>();
        if (testData()) {
            for (int i = 0; i < FIO.size(); i++) {
                if (resppersonHead.contains(appointmentID.get(i)) && actualFlag.get(i).equals(actual))
                    temp.add(FIO.get(i));
            }
        }
        return temp;
    }

    public ArrayList<String> getListBuh(String actual) {
        ArrayList<String> temp = new ArrayList<>();
        if (testData()) {
            for (int i = 0; i < FIO.size(); i++) {
                if (resppersonBuh.contains(appointmentID.get(i)) && actualFlag.get(i).equals(actual))
                    temp.add(FIO.get(i));
            }
        }
        return temp;
    }

    private boolean testData() {
        return appointmentID.size() == FIO.size() && appointmentID.size() == actualFlag.size();
    }

}
