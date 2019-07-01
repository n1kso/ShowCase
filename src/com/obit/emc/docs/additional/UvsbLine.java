package com.obit.emc.docs.additional;

/**
 * @author InferNix
 * @date: 01.06.2009
 */
public class UvsbLine {

    public UvsbLine(String _budgorderID, Double _fullAmnt, Double _amount, String sAmount, String _OperType, boolean _isHead) {
        this._budgorderID = _budgorderID;
        this._fullAmnt = _fullAmnt;
        this._amount = _amount;
        this._OperType = _OperType;
        this._isHead = _isHead;
        this._strAmount = _strAmount;
    }

    /**
     * ������ �� �� (���� BOID)
     * ���� null ������ ��� ������ ���� ������ �� �� ��
     */
    public String getBudgorderID() {
        if (_budgorderID == null) return "";
        return _budgorderID;
    }

    /**
     * ����� ��������� + ��������� � ������ ���� � ��
     */
    public Double getFullAmnt() {
        if (_fullAmnt == null) return -1.0;
        return _fullAmnt;
    }

    /**
     * ����� �����������
     */
    public Double getAmount() {
        if (_amount == null) return -1.0;
        return _amount;
    }

    /**
     * ��� �������� �����������
     */
    public String getOperType() {
        if (_OperType == null) return "";
        return _OperType;
    }

    public boolean getIsHead() {
        return _isHead;
    }

    public String get_strAmount() {
        return _strAmount;
    }

    private String _budgorderID = ""; // ������ �� ��   (���� IDBO)
    private Double _fullAmnt = -1.0;// ����� ��  ��������� + ��������� � ������ ����
    private Double _amount = -1.0; // ����� ����
    private String _OperType = "";// ��� ��������
    private boolean _isHead = false;
    private String _strAmount = "";


}
