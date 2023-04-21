package com.line.secretary.api.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.line.secretary.api.entity.GoogleUserInfo;

@Repository
public interface GoogleUserInfoRepository extends JpaRepository<GoogleUserInfo, Integer> {
    
}
