package br.com.jusbrasil.teste.exception;

public class ParametrosInvalidos extends IllegalArgumentException {

    private static final long serialVersionUID = -7593358302635838120L;

    public ParametrosInvalidos() {
        super();
    }

    public ParametrosInvalidos(String message, Throwable cause) {
        super(message, cause);
    }

    public ParametrosInvalidos(String s) {
        super(s);
    }

    public ParametrosInvalidos(Throwable cause) {
        super(cause);
    }

}
