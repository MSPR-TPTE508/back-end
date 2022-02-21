package fr.epsi.clinic.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.epsi.clinic.model.Staff;
import fr.epsi.clinic.repository.StaffRepository;

@Service
public class StaffService {
    @Autowired
    private StaffRepository staffRepository;

    public Staff getStaffByEmail(String email){
        return staffRepository.getById(email);
    }

    public void addStaff(Staff staff){
        staffRepository.save(staff);
    }
}
