package com.example.api.entity;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "userResponseHistory")
public class UserResponseHistory {

    @Id
    private int userId;
    
    @Column(nullable = false)
    private String lastRequest;

    @Column(nullable = false)
    private String lastResponse;

    @Column(nullable = false)
    private Date lastUpdateTime;
}