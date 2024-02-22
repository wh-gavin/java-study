package springcloud.configclient.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author 一一哥Sun
 * @Date Created in 2020/4/3
 * @Description db4中的用户表
 */
@Entity
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

    @Id
    @GeneratedValue
   // @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String birthday;

    private String sex;

    private String address;

}