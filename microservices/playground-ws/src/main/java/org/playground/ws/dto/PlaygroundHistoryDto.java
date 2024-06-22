package org.playground.ws.dto;

import java.time.LocalDateTime;

public class PlaygroundHistoryDto {
    public LocalDateTime date;
    public String result;

    public PlaygroundHistoryDto(final LocalDateTime date, final String result) {
        this.date = date;
        this.result = result;
    }
}
