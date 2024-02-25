package org.playground.ws.dto;

import org.playground.ws.dao.TemplateDao;

public class TemplateDto {
    String id;
    String title;
    String description;

    public TemplateDto(TemplateDao templateDao) {
        id = templateDao.getId();
        title = templateDao.getTitle();
        description = templateDao.getDescription();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
