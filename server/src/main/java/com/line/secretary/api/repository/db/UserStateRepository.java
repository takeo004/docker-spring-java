package com.line.secretary.api.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.line.secretary.api.entity.UserState;

@Repository
public interface UserStateRepository extends JpaRepository<UserState, Integer> {
    UserState findByUserId(int userId);
}
