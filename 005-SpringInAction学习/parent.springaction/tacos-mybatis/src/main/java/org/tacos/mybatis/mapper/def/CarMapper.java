package org.tacos.mybatis.mapper.def;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.tacos.mybatis.pojo.Car;

@Mapper
public interface CarMapper {
	public List<Car> getAll();

	/**
	 * 插入汽车
	 * 
	 * @return
	 * @param car
	 */
	public int insert(Car car);

	/**
	 * 按id删除车辆信息
	 * 
	 * @param id
	 * @return
	 */
	public int delete(Long id);

	/**
	 * 更新车辆信息
	 * 
	 * @param car
	 * @return
	 */
	public int update(Car car);
	
	 
	@Select("SELECT * FROM car WHERE id = #{id}")
	public Car findById(@Param("id") Long id);
}
