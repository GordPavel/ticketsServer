package sample;

import asg.cliche.*;
import settings.SettingsManager;

import java.io.IOException;

public class EnteredInterface implements ShellDependent{

    private Shell shell;

    @Override
    public void cliSetShell( Shell shell ){
        this.shell = shell;
    }

    @Command( description = "Lists all databases.", abbrev = "-l", name = "--list" )
    public void listAllDatabases(){
        SettingsManager.settings.listBases().forEach( System.out::println );
    }

    @Command( description = "Change the admin's name.", abbrev = "-cN", name = "--changeName" )
    public void changeName(
            @Param( description = "New admin's name", name = "New name" )
                    String name ){
        SettingsManager.setAdminName( name );
        System.out.println( "To accept all changes please restart server. ( command <restart> )" );
    }

    @Command( description = "Change the admin's password.", abbrev = "-cP", name = "--changePassword" )
    public void changePassword(
            @Param( description = "New admin's password", name = "New pass" )
                    String pass ){
        SettingsManager.setAdminPassword( pass );
        System.out.println( "To accept all changes please restart server. ( command <restart> )" );
    }

    @Command( description = "Change the live time of each database executor in cache.", abbrev = "-cT", name = "--changeTimeout" )
    public void changeTimeout(
            @Param( description = "New cache time", name = "New time" )
                    Long timeout ){
        SettingsManager.setCacheTimeout( timeout );
        System.out.println( "To accept all changes please restart server. ( command <restart> )" );
    }

    @Command( description = "Add path of new database", name = "--newDatabase", abbrev = "-nD" )
    public void addNewDatabase(
            @Param( description = "Path to *.far file which contains database.", name = "path" )
                    String path ){
        SettingsManager.addNewBase( path );
        System.out.println( "To accept all changes please restart server. ( command <restart> )" );
    }

    @Command( description = "Delete the path of database from settings file.", abbrev = "-dD", name = "--deleteDatabase" )
    public void deleteDatabase(
            @Param( description = "Path to *.far file which contains database.", name = "path" )
                    String path ){
        SettingsManager.deleteBase( path );
        System.out.println( "To accept all changes please restart server. ( command <restart> )" );
    }

    @Command( description = "Open submenu to configure specified database. Don't forget to restart server after all " +
                            "changes.", abbrev = "-mD", name = "--manageDatabase" )
    public void manageDatabase(
            @Param( description = "Path to *.far file which contains database.", name = "path" )
                    String path ) throws IOException{
        ShellFactory.createSubshell( SettingsManager.settings.getAdminName() + "/" +
                                     path.substring( path.lastIndexOf( "/" ) , path.lastIndexOf( "." ) ) , shell ,
                                     path , new DatabaseInterface( SettingsManager.getBase( path ).orElseThrow(
                        () -> new IllegalArgumentException( "Server doesn't have database " + path ) ) ) )
                    .commandLoop();
    }

    @Command( description = "Start server" )
    public void start(){
//        todo: Запуск сервера
    }

    @Command( description = "Stop server when it can" )
    public void stop(){
//        todo: Остановка сервера
    }

    @Command( description = "Immediately stop server" )
    public void kill(){
//        todo: Немедленная остановка сервера
    }

    @Command( description = "Immediately restart server" )
    public void restart(){
//        todo: Перезапуск сервера
    }
}
