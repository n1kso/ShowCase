package com.obit.emc.docs.additional;

public class MoneyTransferOrderLine {

    private String kesrCode; // КОСГУ
    private String kesrAnalKind; // Бюджетная классификация КОСГУ
    private String codeFSR; // КВФО
    private Double amount; // Сумма
    private String code; // Отраслевой код?

    public MoneyTransferOrderLine(String code, String kesr_code, String kesr_anal_kind, Double amount, String codeFSR) {
        this.kesrCode = kesr_code;
        this.kesrAnalKind = kesr_anal_kind;
        this.codeFSR = codeFSR;
        this.amount = amount;
        this.code = code;
    }

    public MoneyTransferOrderLine(String code, String kesr_code, Double amount) {
        kesrCode = kesr_code;
        this.amount = amount;
        this.code = code;
    }

    public String getKesrCode() {
        if (kesrCode == null) return "";
        return kesrCode;
    }

    public String getKesrAnalKind() {
        return kesrAnalKind == null ? "" : kesrAnalKind;
    }

    public String getCodeFSR() {
        if (codeFSR == null) return "";
        return codeFSR;
    }

    public Double getAmount() {
        if (amount == null) return 0.0;
        return amount;
    }

    public String getCode() {
        return code;
    }
}
