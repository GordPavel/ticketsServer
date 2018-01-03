package settings;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@XmlRootElement
public class Settings implements Cloneable{
    private String adminName;
    private String rootPassword;
    private Long   cacheTimeout;
    private List<Base> base = new ArrayList<>();


    public String getAdminName(){
        return adminName;
    }

    @XmlElement
    void setAdminName( String adminName ){
        this.adminName = adminName;
    }

    public String getRootPassword(){
        return rootPassword;
    }

    @XmlElement
    void setRootPassword( String rootPassword ){
        this.rootPassword = rootPassword;
    }

    public List<Base> getBase(){
        return base;
    }

    public Stream<String> listBases(){
        return base.stream()
                   .map( base1 -> String.format( "%s %-7s\n" , base1.getPath() , base1.isRunning() ? "running" : "" ) );
    }

    @XmlElement
    void setBase( List<Base> base ){
        this.base = base;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder( String.format( "%s - %s\n" , adminName , rootPassword ) );
        for( Base base : base ){
            stringBuilder.append( base.toString() );
        }
        return stringBuilder.toString();
    }

    @Override
    public int hashCode(){
        Integer integer = adminName.hashCode() ^ rootPassword.hashCode();
        for( Base base : this.base ){
            integer ^= base.hashCode();
        }
        return integer;
    }

    @Override
    public boolean equals( Object obj ){
        if( !( obj instanceof Settings ) ) return false;
        Settings settings = ( Settings ) obj;
        if( this.base.size() != settings.base.size() ) return false;
        Iterator<Base> settingsIterator = settings.base.iterator();
        for( Base base : this.base ){
            if( !base.equals( settingsIterator.next() ) ) return false;
        }
        return this.adminName.equals( settings.adminName ) && this.rootPassword.equals( settings.rootPassword );
    }

    @Override
    protected Object clone() throws CloneNotSupportedException{
        Settings clone = ( Settings ) super.clone();
        clone.base = base.stream().map( base1 -> {
            try{
                return ( Base ) base1.clone();
            }catch( CloneNotSupportedException e ){
                throw new IllegalStateException( e );
            }
        } ).collect( Collectors.toList() );
        return clone;
    }

    public Long getCacheTimeout(){
        return cacheTimeout;
    }

    @XmlElement
    void setCacheTimeout( Long cacheTimeout ){
        this.cacheTimeout = cacheTimeout;
    }
}
