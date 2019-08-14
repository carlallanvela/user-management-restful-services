package com.carlallanvela.rest.webservices.usermanagementrestfulservices;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.carlallanvela.rest.webservices.usermanagementrestfulservices.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

}
