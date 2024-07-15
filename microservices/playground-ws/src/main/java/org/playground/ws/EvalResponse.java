package org.playground.ws;

import java.time.LocalDateTime;

public class EvalResponse {

    private String result;
    private String status;
    private LocalDateTime date;

    public EvalResponse(final String result, final String status) {
        this.result = result;
        this.status = status;
        this.date = LocalDateTime.now();
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
