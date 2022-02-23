package fr.epsi.clinic.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.naming.Name;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;

import fr.epsi.clinic.mapper.StaffAttributesMapper;
import fr.epsi.clinic.model.Staff;
import fr.epsi.clinic.repository.StaffRepository;

@Service
public class StaffService {

    @Autowired 
    LdapTemplate ldapTemplate;
    
    @Autowired
    private StaffRepository staffRepository;

    @Value("${ldap.domain}")
    private String activeDirectoryDomain;

    private StaffAttributesMapper staffAttributesMapper = new StaffAttributesMapper();

    public void saveStaff(Staff staff){
        staffRepository.save(staff);
    }

    public Optional<Staff> findUserByEmail(String email){

        //Avoid SQL exception
        if(Objects.isNull(email)){
            email = "";
        }

        return this.staffRepository.findById(email);
    }

    
    public Name buildDn(String username){
        return LdapNameBuilder.newInstance().add("cn", username).build();
    }

    public Staff findStaffInActiveDirectory(String userPrincipalName){
        List<Staff> foundStaffs = ldapTemplate.search(LdapQueryBuilder.query().where("objectclass").is("person").and(LdapQueryBuilder.query().where("userprincipalname").is(userPrincipalName+"@"+activeDirectoryDomain)), staffAttributesMapper);

        if(foundStaffs.size() > 0){
            return foundStaffs.get(0);
        }

        return new Staff();
    }

}
