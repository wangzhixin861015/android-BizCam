package com.bcnetech.bcnetchhttp.bean.data;

public class LightThan {
    private int auxiliaryValue;
    private int backValue;
    private int bottomValue;
    private int leftValue;
    private int moveValue2Value;
    private String name;
    private int rightValue;
    private String version;

    public int getAuxiliaryValue() {
        return auxiliaryValue;
    }

    public void setAuxiliaryValue(int auxiliaryValue) {
        this.auxiliaryValue = auxiliaryValue;
    }

    public int getBackValue() {
        return backValue;
    }

    public void setBackValue(int backValue) {
        this.backValue = backValue;
    }

    public int getBottomValue() {
        return bottomValue;
    }

    public void setBottomValue(int bottomValue) {
        this.bottomValue = bottomValue;
    }

    public int getLeftValue() {
        return leftValue;
    }

    public void setLeftValue(int leftValue) {
        this.leftValue = leftValue;
    }

    public int getMoveValue2Value() {
        return moveValue2Value;
    }

    public void setMoveValue2Value(int moveValue2Value) {
        this.moveValue2Value = moveValue2Value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRightValue() {
        return rightValue;
    }

    public void setRightValue(int rightValue) {
        this.rightValue = rightValue;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "LightThan{" +
                "auxiliaryValue=" + auxiliaryValue +
                ", backValue=" + backValue +
                ", bottomValue=" + bottomValue +
                ", leftValue=" + leftValue +
                ", moveValue2Value=" + moveValue2Value +
                ", name='" + name + '\'' +
                ", rightValue=" + rightValue +
                '}';
    }
}