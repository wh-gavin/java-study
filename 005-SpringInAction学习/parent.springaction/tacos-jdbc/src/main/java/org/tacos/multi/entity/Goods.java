package org.tacos.multi.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author 一一哥Sun
 * @Date Created in 2020/4/3
 * @Description db1中的商品表
 */
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "goods")
@Data
public class Goods {

    @Id
   // @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String name;

}
