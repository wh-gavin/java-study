package fastjson.springboot;


import java.io.Serializable;


public class User implements Serializable {
    private static final long serialVersionUID = 1L;
 
    private String id;
    private String name;
    private String profession;
 
    public String getId() {
        return id;
    }
 
    public void setId(String id) {
        this.id = id;
    }
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public String getProfession() {
        return profession;
    }
 
    public void setProfession(String profession) {
        this.profession = profession;
    }
 
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", profession='" + profession + '\'' +
                '}';
    }
}
 