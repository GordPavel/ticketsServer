package settings;


import settings.serverexceptions.CopyBase;
import settings.serverexceptions.CopyUser;
import settings.serverexceptions.IllegalBasePath;
import settings.serverexceptions.StartStopBaseException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Predicate;

public class SettingsManager{
    private final static String defaultSettingsFileString =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n<settings/>";

    public static  Settings    settings;
    private static JAXBContext jaxbContext;
    private static String      settingsFilePath;

    static{
        try{
            settingsFilePath =
                    new File( SettingsManager.class.getProtectionDomain().getCodeSource().getLocation().toURI() )
                            .getParent() + "/serverfiles/settings.xml";
        }catch( URISyntaxException e ){
            e.printStackTrace();
            System.exit( 1 );
        }
        try{
            jaxbContext = JAXBContext.newInstance( Settings.class , Base.class , User.class );
        }catch( JAXBException e ){
            e.printStackTrace();
            System.exit( 1 );
        }
        settings = loadSettings();
    }

    public static Settings loadSettings(){
        if( !Files.exists( Paths.get( settingsFilePath ) ) ){
            Path filePath = Paths.get( settingsFilePath );
            try{
                Files.createFile( filePath );
                Files.write( filePath , defaultSettingsFileString.getBytes( StandardCharsets.UTF_8 ) );
            }catch( IOException e ){
                e.printStackTrace();
            }
        }
        try( InputStream inputStream = new FileInputStream( settingsFilePath ) ){
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            settings = ( Settings ) jaxbUnmarshaller.unmarshal( inputStream );
        }catch( JAXBException e ){
            System.out.println(
                    "Your settings.xml file is damaged. Do you want to delete it and restart program? [y/n]" +
                    "All your settings'll be lost." );
            Scanner scanner = new Scanner( System.in );
            String  answer;
            while( !( answer = scanner.next() ).matches( "[yn]" ) ){
                System.out.println( "Type y - yes, delete file; or n - no, not delete." );
            }
            if( answer.equals( "y" ) ){
                try{
                    Files.delete( Paths.get( settingsFilePath ) );
                }catch( IOException e1 ){
                    e1.printStackTrace();
                }
            }
            System.exit( 1 );
        }catch( IOException e ){
            e.printStackTrace();
            System.exit( 1 );
        }
        return settings;
    }

    public static void saveSettings(){
        try( OutputStream outputStream = new FileOutputStream( settingsFilePath ) ){
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT , true );
            jaxbMarshaller.marshal( settings , outputStream );
        }catch( JAXBException | IOException e ){
            e.printStackTrace();
        }
    }

    public static void setAdminName( String name ){
        settings.setAdminName( name );
        saveSettings();
    }

    public static void setAdminPassword( String password ){
        settings.setRootPassword( password );
        saveSettings();
    }

    public static void setCacheTimeout( Long timeout ){
        settings.setCacheTimeout( timeout );
        saveSettings();
    }

    public static Optional<Base> getBase( String path ){
        return settings.getBase().stream().filter( base -> base.getPath().equals( path ) ).findAny();
    }

    public static void addNewBase( String path ){
        if( settings.getBase().stream().noneMatch( getBaseEqualByPathPredicate( path ) ) ){
            Base base = new Base( path );
            base.setRunning( false );
            settings.getBase().add( base );
            saveSettings();
        }else{
            throw new CopyBase( "Server already contains this base " + path );
        }
    }

    public static void deleteBase( String path ){
        settings.getBase().removeIf( getBaseEqualByPathPredicate( path ) );
        saveSettings();
    }

    public static void startStopBase( String path , Boolean start ){
        Optional<Base> optionalBase =
                settings.getBase().stream().filter( getBaseEqualByPathPredicate( path ) ).findFirst();
        if( optionalBase.isPresent() ){
            Base editingBase = optionalBase.get();
            if( editingBase.isRunning() && start ){
                throw new StartStopBaseException( "Base " + path + " is already running. Stop it at first." );
            }else if( !editingBase.isRunning() && !start ){
                throw new StartStopBaseException( "Base " + path + " is already stopped. Start it at first." );
            }else{
                editingBase.setRunning( start );
                saveSettings();
            }
        }else{
            throw new IllegalBasePath( "Server doesn't contain this base " + path );
        }
    }

    public static void addNewClient( String basePath , String clientName , String clientPassword ,
                                     UserPrivileges privileges ){
        Optional<Base> optionalBase =
                settings.getBase().stream().filter( getBaseEqualByPathPredicate( basePath ) ).findFirst();
        if( optionalBase.isPresent() ){
            Base editingBase = optionalBase.get();
            if( editingBase.getUser().stream().noneMatch( getUserEqualByNamePredicate( clientName ) ) ){
                editingBase.getUser().add( new User( clientName , clientPassword , privileges ) );
                saveSettings();
            }else{
                throw new CopyUser( "Base " + basePath + " already has this client " + clientName );
            }
        }else{
            throw new IllegalBasePath( "Server doesn't contain this base " + basePath );
        }
    }

    public static void deleteClient( String basePath , String clientName ){
        Optional<Base> optionalBase =
                settings.getBase().stream().filter( getBaseEqualByPathPredicate( basePath ) ).findFirst();
        if( optionalBase.isPresent() ){
            Base editingBase = optionalBase.get();
            if( editingBase.getUser().stream().anyMatch( getUserEqualByNamePredicate( clientName ) ) ){
                editingBase.getUser().removeIf( getUserEqualByNamePredicate( clientName ) );
                saveSettings();
            }else{
                throw new CopyUser( "Base " + basePath + " doesn't have this client " + clientName );
            }
        }else{
            throw new IllegalBasePath( "Server doesn't contain this base " + basePath );
        }
    }

    public static void changeClientName( String basePath , String oldName , String newName ){
        Optional<Base> optionalBase =
                settings.getBase().stream().filter( getBaseEqualByPathPredicate( basePath ) ).findFirst();
        if( optionalBase.isPresent() ){
            Base editingBase = optionalBase.get();
            Optional<User> optionalUser =
                    editingBase.getUser().stream().filter( getUserEqualByNamePredicate( oldName ) ).findFirst();
            if( optionalUser.isPresent() ){
                optionalUser.get().setName( newName );
                saveSettings();
            }else{
                throw new CopyUser( "Base " + basePath + " doesn't have this client " + oldName );
            }
        }else{
            throw new IllegalBasePath( "Server doesn't contain this base " + basePath );
        }
    }

    public static void changeClientPassword( String basePath , String userName , String newPassword ){
        Optional<Base> optionalBase =
                settings.getBase().stream().filter( getBaseEqualByPathPredicate( basePath ) ).findFirst();
        if( optionalBase.isPresent() ){
            Base editingBase = optionalBase.get();
            Optional<User> optionalUser =
                    editingBase.getUser().stream().filter( getUserEqualByNamePredicate( userName ) ).findFirst();
            if( optionalUser.isPresent() ){
                optionalUser.get().setPassword( newPassword );
                saveSettings();
            }else{
                throw new CopyUser( "Base " + basePath + " doesn't have this client " + userName );
            }
        }else{
            throw new IllegalBasePath( "Server doesn't contain this base " + basePath );
        }
    }

    public static void changeClientPrivilege( String basePath , String userName ){
        Optional<Base> optionalBase =
                settings.getBase().stream().filter( getBaseEqualByPathPredicate( basePath ) ).findFirst();
        if( optionalBase.isPresent() ){
            Base editingBase = optionalBase.get();
            Optional<User> optionalUser =
                    editingBase.getUser().stream().filter( getUserEqualByNamePredicate( userName ) ).findFirst();
            if( optionalUser.isPresent() ){
                User user = optionalUser.get();
                user.setPrivilege(
                        user.getPrivilege() == UserPrivileges.Read ? UserPrivileges.ReadWrite : UserPrivileges.Read );
                saveSettings();
            }else{
                throw new CopyUser( "Base " + basePath + " doesn't have this client " + userName );
            }
        }else{
            throw new IllegalBasePath( "Server doesn't contain this base " + basePath );
        }
    }

    private static Predicate<Base> getBaseEqualByPathPredicate( String path ){
        return base -> base.getPath().equals( path );
    }

    private static Predicate<User> getUserEqualByNamePredicate( String clientName ){
        return user -> user.getName().equals( clientName );
    }
}




