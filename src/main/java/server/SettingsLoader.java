package server;

import server.serverexceptions.CopyBase;
import server.serverexceptions.CopyUser;
import server.serverexceptions.IllegalBasePath;
import server.serverexceptions.StartStopBaseException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.function.Predicate;

public class SettingsLoader{

    static         Settings    settings;
    private static JAXBContext jaxbContext;

    static{
        try{
            jaxbContext = JAXBContext.newInstance( Settings.class , Base.class , User.class );
        }catch( JAXBException e ){
            e.printStackTrace();
        }
        settings = loadSettings();
    }

    public static Settings loadSettings(){
        if( settings == null ){
            try( InputStream inputStream = new FileInputStream(
                    new File( SettingsLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI() ) +
                    "/serverfiles/settings.xml" ) ){
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                settings = ( Settings ) jaxbUnmarshaller.unmarshal( inputStream );
            }catch( JAXBException | IOException | URISyntaxException e ){
                e.printStackTrace();
            }
        }
        return settings;
    }

    public static void saveSettings(){
        try( OutputStream outputStream = new FileOutputStream(
                new File( SettingsLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI() )
                        .getParent() + "/serverfiles/settings.xml" ) ){
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            // output pretty printed
            jaxbMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT , true );
            jaxbMarshaller.marshal( settings , outputStream );
        }catch( JAXBException | IOException | URISyntaxException e ){
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

    public static void removeBase( String path ){
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

    private static Predicate<Base> getBaseEqualByPathPredicate( String path ){
        return base -> base.getPath().equals( path );
    }

    private static Predicate<User> getUserEqualByNamePredicate( String clientName ){
        return user -> user.getName().equals( clientName );
    }
}




