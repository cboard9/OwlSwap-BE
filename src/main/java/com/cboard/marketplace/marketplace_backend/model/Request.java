package com.cboard.marketplace.marketplace_backend.model;

import jakarta.persistence.Entity;
import com.cboard.marketplace.marketplace_common.*;

import jakarta.persistence.*;

@Entity
@Table(name = "request")
//@DiscriminatorValue("request")
public class Request extends Item
{
    private String deadline;

    public Request() {
    }

    public Request(String deadline) {
        this.deadline = deadline;
    }


    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}