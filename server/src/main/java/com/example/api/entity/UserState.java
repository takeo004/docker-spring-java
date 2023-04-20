package com.example.api.entity;

import java.util.Date;

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
@Table(name = "userState")
public class UserState {
    
    @Id
    private int userId;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String stateDetail;

    @Column
    private String note;
    
    @Column(nullable = false)
    private Date lastUpdateTime;
}
