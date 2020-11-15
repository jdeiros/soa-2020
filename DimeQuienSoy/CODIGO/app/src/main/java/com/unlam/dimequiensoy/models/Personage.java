package com.unlam.dimequiensoy.models;

public class Personage {
    private String Name;
    private boolean alreadyUsed;
    private boolean okay;
    private int position;
    public Personage(int position, String name, boolean alreadyUsed, boolean okay) {
        this.position = position;
        Name = name;
        this.alreadyUsed = alreadyUsed;
        this.okay = okay;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Personage() {
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public boolean isAlreadyUsed() {
        return alreadyUsed;
    }

    public void setAlreadyUsed(boolean alreadyUsed) {
        this.alreadyUsed = alreadyUsed;
    }

    public boolean isOkay() {
        return okay;
    }

    public void setOkay(boolean okay) {
        this.okay = okay;
    }
}
