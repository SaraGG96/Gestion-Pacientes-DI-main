package com.clase.modelo;

public class Doctor {

    private String iddoc;
    private String apeldoc;
    private String nomedoc;
    private String tlfodoc;
    private String emaildoc;
    private String locpac;
    private boolean colegiado;

    public Doctor(String iddoc, String apeldoc, String nomedoc,
            String tlfodoc, String emaildoc, String locpac,
            boolean colegiado) {
        this.iddoc = iddoc;
        this.apeldoc = apeldoc;
        this.nomedoc = nomedoc;
        this.tlfodoc = tlfodoc;
        this.emaildoc = emaildoc;
        this.locpac = locpac;
        this.colegiado = colegiado;
    }

    public String getIddoc() {
        return iddoc;
    }

    public void setIddoc(String iddoc) {
        this.iddoc = iddoc;
    }

    public String getApeldoc() {
        return apeldoc;
    }

    public void setApeldoc(String apeldoc) {
        this.apeldoc = apeldoc;
    }

    public String getNomedoc() {
        return nomedoc;
    }

    public void setNomedoc(String nomedoc) {
        this.nomedoc = nomedoc;
    }

    public String getTlfodoc() {
        return tlfodoc;
    }

    public void setTlfodoc(String tlfodoc) {
        this.tlfodoc = tlfodoc;
    }

    public String getEmaildoc() {
        return emaildoc;
    }

    public void setEmaildoc(String emaildoc) {
        this.emaildoc = emaildoc;
    }

    public String getLocpac() {
        return locpac;
    }

    public void setLocpac(String locpac) {
        this.locpac = locpac;
    }

    public boolean isColegiado() {
        return colegiado;
    }

    public void setColegiado(boolean colegiado) {
        this.colegiado = colegiado;
    }
}
