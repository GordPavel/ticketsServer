package settings;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@XmlRootElement
public class Base implements Cloneable{
    private String     path;
    private Boolean    isRunning;
    private List<User> user;

    public Base(){
    }

    Base( String path ){
        this.path = path;
        isRunning = false;
        user = new ArrayList<>();
    }

    Base( String path , Boolean isRunning , List<User> user ){
        this.path = path;
        this.isRunning = isRunning;
        this.user = user;
    }

    public String getPath(){
        return path;
    }

    @XmlAttribute
    void setPath( String path ){
        this.path = path;
    }

    public Boolean isRunning(){
        return isRunning;
    }

    @XmlElement
    public void setRunning( Boolean running ){
        isRunning = running;
    }

    List<User> getUser(){
        return user;
    }

    public Stream<String> listAllUsers(){
        return user.stream().map( user1 -> String
                .format( "%-2s %10s password:%s" , user1.getPrivilege() == UserPrivileges.ReadWrite ? "rw" : "r" ,
                         user1.getName() , user1.getPassword() ) );
    }

    @XmlElement
    void setUser( List<User> user ){
        this.user = user;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder( String.format( "   %s\n" , path ) );
        for( User user : user ){
            stringBuilder.append( user.toString() );
        }
        return stringBuilder.toString();
    }

    @Override
    public int hashCode(){
        Integer integer = path.hashCode();
        for( User user : this.user ){
            integer ^= user.hashCode();
        }
        return integer;
    }

    @Override
    public boolean equals( Object obj ){
        if( !( obj instanceof Base ) ) return false;
        Base base = ( Base ) obj;
        if( this.user.size() != base.user.size() ) return false;
        Iterator<User> userIterator = base.user.iterator();
        for( User user : this.user ){
            if( !user.equals( userIterator.next() ) ) return false;
        }
        return this.path.equals( base.path );
    }

    @Override
    protected Object clone() throws CloneNotSupportedException{
        Base clone = ( Base ) super.clone();
        clone.user = user.stream().map( user1 -> {
            try{
                return ( User ) user1.clone();
            }catch( CloneNotSupportedException e ){
                throw new IllegalStateException( e );
            }
        } ).collect( Collectors.toList() );
        return clone;
    }
}
