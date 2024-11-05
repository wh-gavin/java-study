package fastjson.springboot;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;
 
@Repository
public class UserDao{
 
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<User>();
        User user = new User();
        user.setId("1");
        user.setName("xzw");
        user.setProfession("p1");
        list.add(user);
        
        User user1 = new User();
        user1.setId("2");
        user1.setName("xzw2");
        user1.setProfession("p2");
        list.add(user1);
        
        return list;
    }
 
 
}
 