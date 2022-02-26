package fr.epsi.clinic.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.epsi.clinic.model.VerificationInformationToken;

@Repository
public interface VerificationInformationTokenRepository extends JpaRepository<VerificationInformationToken, String>{
    Optional<VerificationInformationToken> findBySecret(String secret);
}
