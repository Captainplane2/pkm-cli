package com.example.pkm_web.repository;

import com.example.pkm_web.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {     //接口类声明的这些方法，其具体实现由框架（Spring Data JPA）自动生成。
    Optional<User> findByUsername(String username);     //Optional<User> 是 Java 8 引入的一个容器类，用于表示可能包含或不包含非空值的对象。它的主要作用是避免直接返回 null，从而减少空指针异常（NullPointerException）的风险。
    Optional<User> findByEmail(String email);           //根据邮箱查找用户。如果找到匹配的用户，则返回包含该用户的 Optional；否则返回一个空的 Optional。
    Optional<User> findByUsernameOrEmail(String username, String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
