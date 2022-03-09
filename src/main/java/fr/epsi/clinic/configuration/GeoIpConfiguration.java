package fr.epsi.clinic.configuration;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;

import org.springframework.context.annotation.Configuration;

@Configuration
public class GeoIpConfiguration {
    private final String geoIpDatabaseFilename = "GeoLite2-Country.mmdb";
    private DatabaseReader dbReader;

    public GeoIpConfiguration(){
        try {
            InputStream inputFile = GeoIpConfiguration.class.getClassLoader().getResourceAsStream(geoIpDatabaseFilename);
            DatabaseReader dbReader = new DatabaseReader.Builder(inputFile).build();

            this.dbReader = dbReader;
        } catch(Exception e){
            
        }
    }

    public DatabaseReader getDbReader() {
        return dbReader;
    }

    
    
}
