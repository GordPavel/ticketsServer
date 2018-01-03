package sample;

import asg.cliche.Command;
import asg.cliche.Param;
import asg.cliche.Shell;
import asg.cliche.ShellFactory;
import settings.Settings;
import settings.SettingsManager;

import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

public class Interface{

    private static Shell    mainShell;
    private static Settings settings;

    static{
        settings = SettingsManager.loadSettings();
    }

    @Command( description = "Log in the system to manage server.", abbrev = "-l", name = "--login" )
    public void login(
            @Param( description = "admin's name", name = "Admin name" )
                    String login ,
            @Param( description = "admin's password", name = "Admin password" )
                    String password ) throws IOException{
        if( !login.equals( settings.getAdminName() ) ){
            System.out.println( "Illegal admin name" );
            return;
        }
        if( !password.equals( settings.getRootPassword() ) ){
            System.out.println( "Illegal admin password" );
            return;
        }
        System.out.println( "Successfully entered." );
        ShellFactory.createSubshell( SettingsManager.settings.getAdminName() , mainShell , "server" ,
                                     new EnteredInterface() ).commandLoop();
    }

    public static void main( String[] params ) throws IOException{
        if( !Optional.ofNullable( SettingsManager.settings.getAdminName() ).isPresent() ) firstSet();
        mainShell = ShellFactory.createConsoleShell( "" , "" , new Interface() );
        mainShell.commandLoop();
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