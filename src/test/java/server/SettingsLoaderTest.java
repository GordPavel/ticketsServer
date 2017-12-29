package server;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SettingsLoaderTest{

    private Settings settings;

    @BeforeEach
    void setUp(){
        settings = new Settings();
        settings.setAdminName( "admin" );
        settings.setRootPassword( "adminPass" );
        settings.setBase( Stream.of( new Base( "/serverfiles/settings.xml" , true ,
                                               Stream.of( new User( "user" , "pass" , UserPrivileges.ReadWrite ) ,
                                                          new User( "user1" , "pass" , UserPrivileges.Read ) )
                                                     .collect( Collectors.toList() ) ) ,
                                     new Base( "/serverfiles/settings1.xml" , false ,
                                               Stream.of( new User( "user" , "pass" , UserPrivileges.ReadWrite ) ,
                                                          new User( "user1" , "pass" , UserPrivileges.Read ) )
                                                     .collect( Collectors.toList() ) ) )
                                .collect( Collectors.toList() ) );
    }

    @Test
    void stringRepresentation(){
        System.out.println( settings );
    }

    @Test
    void xmlRepresentation() throws CloneNotSupportedException{
        SettingsLoader.settings = settings;
        SettingsLoader.saveSettings();
        Settings copy = ( Settings ) SettingsLoader.settings.clone();
        assertEquals( copy , SettingsLoader.loadSettings() , "Equal settings" );
    }
}