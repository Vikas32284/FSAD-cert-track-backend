package com.institute.skill7.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.institute.skill7.model.Certificate;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {

    List<Certificate> findByUserEmail(String userEmail);

}