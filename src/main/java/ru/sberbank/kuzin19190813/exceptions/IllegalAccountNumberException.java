package ru.sberbank.kuzin19190813.exceptions;

public class IllegalAccountNumberException extends Exception {
    public IllegalAccountNumberException() {

    }

    public IllegalAccountNumberException(String message) {
        super(message);
    }
}
