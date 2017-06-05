package cn.com.zlqf.airservice.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import cn.com.zlqf.airservice.entity.FlyDynamic;
import cn.com.zlqf.airservice.entity.User;

public interface FlyDynamicDao extends JpaRepository<FlyDynamic, String>{
	
	@Query(value="select * from t_fly_dynamic where time=?1 order by state,addtime",nativeQuery=true)
	List<FlyDynamic> getFlyDynamicListByTime(String time);
}
