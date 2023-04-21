package com.example.api.entity;

import java.util.Date;

import com.example.api.constant.State;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userStateId;

    @Column
    private int userId;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false)
    private String stateDetail;

    @Column
    private String note;
    
    @Column(nullable = false)
    private Date lastUpdateTime;

    public UserState(int userId, State state, String note) {
        this.userId = userId;
        this.state = state.getState();
        this.stateDetail = state.getDetail();
        this.note = note;
        this.lastUpdateTime = new Date();
    }
}
