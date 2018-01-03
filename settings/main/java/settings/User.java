package settings;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class User implements Cloneable{
    private String         name;
    private String         password;
    private UserPrivileges privilege;

    public User(){
    }

    User( String name , String password , UserPrivileges privilege ){
        this.name = name;
        this.password = password;
        this.privilege = privilege;
    }

    String getName(){
        return name;
    }

    @XmlAttribute
    void setName( String name ){
        this.name = name;
    }

    String getPassword(){
        return password;
    }

    @XmlElement
    void setPassword( String password ){
        this.password = password;
    }

    UserPrivileges getPrivilege(){
        return privilege;
    }

    @XmlElement
    void setPrivilege( UserPrivileges privilege ){
        this.privilege = privilege;
    }

    @Override
    public String toString(){
        return String.format( "         %s\n" + "         %s\n" + "         %s\n\n" , name , password , privilege );
    }

    @Override
    public int hashCode(){
        return name.hashCode() ^ password.hashCode() ^ privilege.hashCode();
    }

    @Override
    public boolean equals( Object obj ){
        if( !( obj instanceof User ) ) return false;
        User user = ( User ) obj;
        return this.name.equals( user.name ) && this.password.equals( user.password ) &&
               this.privilege.equals( user.privilege );
    }

    @Override
    protected Object clone() throws CloneNotSupportedException{
        return super.clone();
    }
}