package org.playground.ws.dto;

import java.time.LocalDateTime;

public class PlaygroundHistoryDto {
    public LocalDateTime date;
    public String result;

    public PlaygroundHistoryDto(final LocalDateTime date, final String result) {
        this.date = date;
        this.result = result;
    }

    public LocalDateTime getDate() {
        return this.date;
    }

    public String getResult() {
        return this.result;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PlaygroundHistoryDto)) {
            return false;
        }
        if (((PlaygroundHistoryDto) obj).getResult().equals(this.result) &&
                ((PlaygroundHistoryDto) obj).getDate().equals(this.date)) {
            return true;
        }
        return false;
    }
}
