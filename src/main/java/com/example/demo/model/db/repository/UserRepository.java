package com.example.demo.model.db.repository;

import com.example.demo.model.db.entity.User;
import com.example.demo.model.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Page<User> findAllByStatusNot(Pageable request, UserStatus status);

    @Query("select u from User u where u.status <> :status and (lower(u.lastName) like %:filter% or lower(u.firstName) like %:filter%)")
    Page<User> findAllByStatusNotFiltered(Pageable request, UserStatus status, @Param("filter") String filter);
}
