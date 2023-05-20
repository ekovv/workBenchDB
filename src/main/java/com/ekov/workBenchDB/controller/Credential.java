package com.ekov.workBenchDB.controller;

public class Credential {

    private String username;
    private String password;
    private String adr;

    public Credential(String adr, String user, String pass) {
        this.adr = adr;
        this.username = user;
        this.password = pass;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAdr() {
        return adr;
    }

    public void setAdr(String adr) {
        this.adr = adr;
    }
}
