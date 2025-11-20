package com.iforddow.authservice.auth.repository.jpa;

import com.iforddow.authservice.auth.entity.jpa.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
* A repository interface for managing Account entities.
* This interface extends JpaRepository to provide CRUD operations
* and additional JPA functionalities for the Account entity.
*
* @author IFD
* @since 2025-11-09
* */
public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findAccountByEmail(String email);

}
