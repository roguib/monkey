package org.monkey.ws;

public class Evaluation {
    private String result;
    private String status;

    public static String SUCCESS = "ok";
    public static String ERROR = "error";

    // TODO: add here errors and other stuff useful for debugging

    public Evaluation() {

    }

    public Evaluation(final String result) {
        this.result = result;
        this.status = SUCCESS;
    }

    public Evaluation(final String result, final String status) {
        this.result = result;
        this.status = status;
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(final String result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
