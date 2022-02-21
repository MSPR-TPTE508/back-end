package fr.epsi.clinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import fr.epsi.clinic.model.Staff;

@Repository
public interface StaffRepository extends JpaRepository<Staff, String>{
    
}
