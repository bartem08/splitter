package com.splitter.userservice.repository;

import com.splitter.userservice.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    @Override
    List<User> findAll();

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
