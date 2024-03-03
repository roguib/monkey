package org.playground.ws.dto;

public class CreatePlaygroundDto {
    private String templateId;

    public CreatePlaygroundDto() {
    }

    public CreatePlaygroundDto(final String templateId) {
        this.templateId = templateId;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }
}
