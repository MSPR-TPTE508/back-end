package fr.epsi.clinic.configuration;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.maxmind.geoip2.DatabaseReader;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeoIpConfiguration {
    private final String geoIpDatabaseFilename = "GeoLite2-Country.mmdb";
    private DatabaseReader dbReader;

    public GeoIpConfiguration(){
        try {
            Path path = Paths.get(getClass().getClassLoader().getResource(this.geoIpDatabaseFilename).toURI());
            File file = path.toFile();
            DatabaseReader dbReader = new DatabaseReader.Builder(file).build();

            this.dbReader = dbReader;
        } catch(Exception e){
            
        }
    }

    public DatabaseReader getDbReader() {
        return dbReader;
    }

    
    
}
