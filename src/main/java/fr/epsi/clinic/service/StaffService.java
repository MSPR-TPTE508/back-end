package fr.epsi.clinic.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.epsi.clinic.model.Staff;
import fr.epsi.clinic.repository.StaffRepository;

@Service
public class StaffService {
    
    @Autowired
    private StaffRepository staffRepository;

    public void addStaff(Staff staff){
        staffRepository.save(staff);
    }

    public Optional<Staff> findUserByEmail(String email){
        return this.staffRepository.findById(email);
    }

}
