package com.obit.emc.docs.additional;

public class PaymentID {

    private String authorID; // ������ ����, ����������� ��������
    private String piAnalKind; // ��� �������������
    private String piBudgetCode; // �������� �������������
    private String payOKATO; // �����
    private String groundID; // ���������� ��������� �������
    private String taxPeriod; // ��������� ������
    private String grndDocNumber; // ����� ���������
    private String grndDocDate; // ���� ���������
    private String payTypeCode; // ��� �������

    public PaymentID(String authorID, String piAnalKind, String piBudgetCode, String payOKATO, String groundID,
                     String taxPeriod, String grndDocNumber, String grndDocDate, String payTypeCode) {
        this.authorID = checkNullandToTrim(authorID);
        this.piAnalKind = checkNullandToTrim(piAnalKind);
        this.piBudgetCode = checkNullandToTrim(piBudgetCode);
        this.payOKATO = checkNullandToTrim(payOKATO);
        this.groundID = checkNullandToTrim(groundID);
        this.taxPeriod = checkNullandToTrim(taxPeriod);
        this.grndDocNumber = checkNullandToTrim(grndDocNumber);
        this.grndDocDate = checkNullandToTrim(grndDocDate);
        this.payTypeCode = checkNullandToTrim(payTypeCode);
    }

    public boolean is_valid() {
        int countFill = 0;
        if (authorID != null && !authorID.equals("")) countFill++;
        if (piBudgetCode != null && !piBudgetCode.equals("")) countFill++;
        if (payOKATO != null && !payOKATO.equals("")) countFill++;
        if (groundID != null && !groundID.equals("")) countFill++;
        if (taxPeriod != null && !taxPeriod.equals("")) countFill++;
        if (grndDocNumber != null && !grndDocNumber.equals("")) countFill++;
        if (grndDocDate != null && !grndDocDate.equals("")) countFill++;
        return (countFill == 0 || countFill == 7);
    }

    public String getAuthorID() {
        return authorID;
    }

    public String getPiAnalKind() {
        return piAnalKind;
    }

    public String getPiBudgetCode() {
        return piBudgetCode;
    }

    public String getPayOKATO() {
        return payOKATO;
    }

    private String getGroundID() {
        return groundID;
    }

    private String getTaxPeriod() {
        return taxPeriod;
    }

    private String getGrndDocNumber() {
        return grndDocNumber;
    }

    public String getGrndDocDate() {
        return grndDocDate;
    }

    public String getPayTypeCode() {
        return payTypeCode;
    }

    /**
     * ��������� ���� �������������� ������� �� �������
     *
     * @return ���� ���� �� ���������, �� ������ true
     */
    public boolean checkForEmptiness() {
        return getAuthorID().length() == 0 &&
                getPiBudgetCode().length() == 0 &&
                getPayOKATO().length() == 0 &&
                getGroundID().length() == 0 &&
                getTaxPeriod().length() == 0 &&
                getGrndDocNumber().length() == 0 &&
                getGrndDocDate().length() == 0;
    }

    public boolean checkCountFillFields() {
        int count = 0;
        if (getAuthorID().length() != 0) count++;
        if (getPiBudgetCode().length() != 0) count++;
        if (getPayOKATO().length() != 0) count++;
        if (getGroundID().length() != 0) count++;
        if (getTaxPeriod().length() != 0) count++;
        if (getGrndDocNumber().length() != 0) count++;
        if (getGrndDocDate().length() != 0) count++;

        return count != 7;
    }

    private String checkNullandToTrim(String value) {
        return value == null ? "" : value.trim();
    }

}
