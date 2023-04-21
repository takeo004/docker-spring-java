package com.line.secretary.api.repository.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.line.secretary.api.entity.UserInfo;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {
    UserInfo findByLineUserId(String lineUserId);
    UserInfo findByUserName(String userName);
}