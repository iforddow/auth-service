package com.iforddow.authservice.auth.repository.jpa;

import com.iforddow.authservice.auth.entity.jpa.TrustedDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TrustedDeviceRepository extends JpaRepository<TrustedDevice, UUID> {

}
