package com.rkp.topcore.canonical.definition;

public class MaskingConfiguration {

    private int showFirst;

    private int showLast;

    private String maskCharacter = "*";


    public int getShowFirst() {
        return showFirst;
    }

    public void setShowFirst(int showFirst) {
        this.showFirst = showFirst;
    }


    public int getShowLast() {
        return showLast;
    }

    public void setShowLast(int showLast) {
        this.showLast = showLast;
    }


    public String getMaskCharacter() {
        return maskCharacter;
    }

    public void setMaskCharacter(String maskCharacter) {
        this.maskCharacter = maskCharacter;
    }
}