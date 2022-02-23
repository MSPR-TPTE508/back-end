package fr.epsi.clinic.mapper;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.springframework.ldap.core.AttributesMapper;

import fr.epsi.clinic.model.Staff;

public class StaffAttributesMapper implements AttributesMapper<Staff>{

    @Override
    public Staff mapFromAttributes(Attributes attributes) throws NamingException {
        Staff staff = new Staff();
        staff.setEmail(attributes.get("mail").get().toString());

        return staff;
    }
    
}
