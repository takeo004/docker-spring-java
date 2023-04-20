package com.example.api.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.api.entity.UserState;

@Repository
public interface UserStateRepository extends JpaRepository<UserState, Integer> {
}
