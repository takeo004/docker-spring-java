package com.example.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "userInitState")
public class UserInitState {
    
    @Column
    private String lineUserId;
}
