package sample;

import asg.cliche.Command;
import server.Settings;
import server.SettingsManager;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Scanner;

public class Interface{

    private static Settings settings;

    static{
        settings = SettingsManager.loadSettings();
    }

    @Command
    void login( String login , String password ){
        if( !login.equals( settings.getAdminName() ) ){
            System.out.println( "Illegal admin name" );
            return;
        }
        if( !password.equals( settings.getRootPassword() ) ){
            System.out.println( "Illegal admin password" );
            return;
        }
    }

    public static void main( String[] params ){
//        System.out.println(
//                new File( SettingsManager.class.getProtectionDomain().getCodeSource().getLocation().toURI() )
//                        .getParent() );
        if( ! Files.exists(  ) ){
            firstSet();
        }
    }

    private static void firstSet(){
        Scanner scanner = new Scanner( System.in );
        System.out.println( "Hello! This is your first start of routes and flights server database.\n" +
                            "( or you've just lost your settings.xml file.\n" +
                            " Stop this app, find settings.xml and move to the same folder with this app)." );
        System.out.print( "Let's configure your database. Enter your admin name> " );
        SettingsManager.setAdminName( scanner.next() );
        System.out.print( "Enter your admin password> " );
        SettingsManager.setAdminPassword( scanner.next() );
        System.out.print( "System stores executor for every database for some time. \n" +
                          "After this time without any request to this database, \n" +
                          "server deletes this executor to balance storage usage and speed.\n" +
                          "Specify this time in milliseconds> " );
        SettingsManager.setCacheTimeout( scanner.nextLong() );
        System.out.println( "Good, now we're ready to work. Type ?l to show all commands." );
    }
}