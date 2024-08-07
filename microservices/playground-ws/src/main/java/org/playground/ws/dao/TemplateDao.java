package org.playground.ws.dao;

import jakarta.persistence.*;
import org.eclipse.persistence.annotations.UuidGenerator;

import java.io.Serializable;

@Entity
@Table(name = "templates", schema="public")
public class TemplateDao implements Serializable {

    @Id
    @GeneratedValue(generator = "UUID")
    @UuidGenerator(name = "UUID")
    @Column(name = "id")
    String id;

    @Column(name = "title")
    String title;

    @Column(name="description")
    String description;

    @Column(name = "program")
    String program;

    @Column(name="result")
    String result;

    public static TemplateDao of(final String title, final String description, final String program, final String result) {
        TemplateDao template = new TemplateDao();
        template.setTitle(title);
        template.setDescription(description);
        template.setProgram(program);
        template.setResult(result);

        return template;
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

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}