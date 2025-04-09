package com.cboard.marketplace.marketplace_backend.model;

import jakarta.persistence.Entity;
import com.cboard.marketplace.marketplace_common.*;

import jakarta.persistence.*;
import org.springframework.stereotype.Component;

@Entity
@DiscriminatorValue("request")
public class Request
{
    @Id
    private int requestId;
    private String deadline;

    public Request() {
    }

    public Request(int requestId, String deadline) {
        this.requestId = requestId;
        this.deadline = deadline;
    }

    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
}