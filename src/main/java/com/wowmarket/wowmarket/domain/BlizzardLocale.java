package com.wowmarket.wowmarket.domain;



public enum BlizzardLocale {
    PT_BR("pt_BR"),
    EN_US("en_US"),
    ES_MX("es_MX"),
    EN_GB("en_GB");

    private final String value;

    BlizzardLocale(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
