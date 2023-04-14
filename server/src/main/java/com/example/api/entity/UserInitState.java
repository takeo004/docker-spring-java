package com.example.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "userInitState")
public class UserInitState {
    
    @Id
    private String lineUserId;
}
