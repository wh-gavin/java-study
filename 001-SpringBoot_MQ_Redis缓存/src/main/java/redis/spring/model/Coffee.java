package redis.spring.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Entity
@Table(name = "T_COFFEE")
@Builder
@Data
@ToString( callSuper = true)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Coffee implements Serializable {
	private static final long serialVersionUID = 6048190202408446083L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private Long price;
	
	@Column(updatable = false)
	@CreationTimestamp
	private Date createTime;
	@UpdateTimestamp
	private Date updateTime;
}
