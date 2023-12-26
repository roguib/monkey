package org.playground.ws;

public class EvalResponse {

    private String result;
    private String status;

    public EvalResponse(final String result, final String status) {
        this.result = result;
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
