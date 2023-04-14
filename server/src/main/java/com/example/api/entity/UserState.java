package com.example.api.entity;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "userState")
public class UserState {
    
    @Id
    private int userId;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String stateDetail;

    @Column(nullable = false)
    private Date lastUpdateTime;
}
