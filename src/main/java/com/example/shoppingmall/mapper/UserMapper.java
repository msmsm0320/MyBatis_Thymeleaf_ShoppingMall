package com.example.shoppingmall.mapper;

import com.example.shoppingmall.domain.user.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    User findByEmail(String email);
    List<String> findRoleNamesByUserId(Long userId); // 특정 사용자의 역할 검색, 사용자는 역할이 여러개 있을 수 있음
    Integer existsByEmail(String email); // email 중복 검사용
    int insertUser(User user);// 사용자 Insert
    int insertUserRole(@Param("id") Long id, @Param("role") String role); // 사용자의 Role Insert

    Long findRefreshVersion(Long userId); // 사용자의 현재 RefreshToken 버전 조회
    int upsertRefreshVersion(
            @Param("userId") Long userId,
            @Param("version") Long version
                             ); // RefreshToken 버전을 새로 삽입 or 업데이트 -> 로그인/재발급할 경우 버전 증가


}
