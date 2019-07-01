package com.obit.emc.docs.additional;

import java.util.Objects;


public class Key {

    private String firstParameter;
    private String secondParameter;
    private String thirdParameter;

    public Key(String firstParameter, String secondParameter, String thirdParameter) {
        this.firstParameter = firstParameter;
        this.secondParameter = secondParameter;
        this.thirdParameter = thirdParameter;
    }

    public Key(String firstParameter, String secondParameter) {
        this.firstParameter = firstParameter;
        this.secondParameter = secondParameter;
    }

    public String getFirstParameter() {
        return firstParameter;
    }

    public String getSecondParameter() {
        return secondParameter;
    }

    String getThirdParameter() {
        return thirdParameter;
    }

    boolean equalsWithoutTypeOperation(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Key key = (Key) o;
        return Objects.equals(firstParameter, key.firstParameter) &&
                Objects.equals(secondParameter, key.secondParameter);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Key key = (Key) o;
        return Objects.equals(firstParameter, key.firstParameter) &&
                Objects.equals(secondParameter, key.secondParameter) &&
                Objects.equals(thirdParameter, key.thirdParameter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstParameter, secondParameter, thirdParameter);
    }


}

