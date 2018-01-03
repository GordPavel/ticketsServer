package server;

import com.sun.istack.NotNull;
import model.DataModel;
import settings.Base;
import settings.Settings;
import settings.SettingsManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server{

    class CacheKey<T>{
        T    key;
        Long timestamp;

        CacheKey( T key , Long timestamp ){
            this.key = key;
            this.timestamp = timestamp;
        }

        @Override
        public int hashCode(){
            int hash = 7;
            hash = 43 * hash + ( this.key != null ? this.key.hashCode() : 0 );
            return hash;
        }

        @Override
        public boolean equals( Object obj ){
            if( obj == null ){
                return false;
            }
            if( !( obj instanceof CacheKey ) ){
                return false;
            }
            CacheKey cacheKey = ( CacheKey ) obj;
            return this.key.equals( cacheKey.key );
        }

        @Override
        public String toString(){
            return String.format( "key[%s]" , key.toString() );
        }
    }

    private Map<CacheKey<Path>, DataModel> bases;
    private Settings                       settings;

    {
        settings = SettingsManager.loadSettings();
        bases = new ConcurrentHashMap<>( ( int ) settings.getBase().stream().filter( Base::isRunning ).count() , 0.75f ,
                                         32 );
        settings.getBase().stream().filter( Base::isRunning ).forEach( base -> {
            Path path = Paths.get( base.getPath() );
//            todo : Неправильные данные в базе
            bases.put( new CacheKey<>( path , System.currentTimeMillis() ) , new DataModel( path.toFile() ) );
        } );
        Executors.newSingleThreadScheduledExecutor( r -> {
            Thread th = new Thread( r );
            th.setDaemon( true );
            return th;
        } ).scheduleAtFixedRate( () -> {
            bases.entrySet().removeIf(
                    cacheKeyDataModelEntry -> System.currentTimeMillis() - cacheKeyDataModelEntry.getKey().timestamp >
                                              settings.getCacheTimeout() );
        } , settings.getCacheTimeout() , settings.getCacheTimeout() , TimeUnit.MILLISECONDS );
    }

    public static void main(
            @NotNull
                    String[] args ){
        int port         = Integer.parseInt( args[ 0 ] );
        int stoppingPort = Integer.parseInt( args[ 1 ] );
        try( ServerSocket serverSocket = new ServerSocket( port ) ){
            ExecutorService service = Executors.newCachedThreadPool();
            while( true ){
//                todo : Останова сервера
                service.execute( new RequestParser( serverSocket.accept() ) );
            }
        }catch( IOException e ){
            e.printStackTrace();
        }
    }

    static class RequestParser implements Runnable{
        private final Socket socket;

        RequestParser( Socket socket ){
            this.socket = socket;
        }

        @Override
        public void run(){


            try{
                socket.close();
            }catch( IOException e ){
                e.printStackTrace();
            }
        }
    }
}
