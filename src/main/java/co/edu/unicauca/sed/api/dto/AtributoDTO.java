package co.edu.unicauca.sed.api.dto;

public class AtributoDTO {
    private String codigoAtributo;
    private String valor;

    public AtributoDTO() {}

    public AtributoDTO(String codigoAtributo, String valor) {
        this.codigoAtributo = codigoAtributo;
        this.valor = valor;
    }

    public String getCodigoAtributo() {
        return codigoAtributo;
    }

    public void setCodigoAtributo(String codigoAtributo) {
        this.codigoAtributo = codigoAtributo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
