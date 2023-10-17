package com.farneser.cloudfilestorage.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Data;

@Data
@Entity
public class Session {
    @Id
    private String sessionId;
    @Lob
    private byte[] sessionData;
}