package com.example.api.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.api.entity.UserInitState;

@Repository
public interface UserInitStateRepository extends JpaRepository<UserInitState, Integer>  {
    UserInitState findByLineUserId(String lineUserId);
}
