package com.catalog.midiacatalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.catalog.midiacatalog.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

}
