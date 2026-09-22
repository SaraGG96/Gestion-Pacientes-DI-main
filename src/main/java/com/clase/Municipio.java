package com.clase;


public class Municipio {
    public String id;
    public String nm;

    public String getId() { return id; }
    public String getNm() { return nm; }

    // El ID del municipio (p. ej. "15001") empieza por los 2 dígitos de la provincia (p. ej. "15" para A Coruña)
    public String getCodigoProvincia() {
        return (id != null && id.length() >= 2) ? id.substring(0, 2) : "";
    }

    @Override
    public String toString() {
        return nm;
    }
}