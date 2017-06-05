package cn.com.zlqf.airservice.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cn.com.zlqf.airservice.entity.FlyDynamic;
import cn.com.zlqf.airservice.entity.FlyInfo;
import cn.com.zlqf.airservice.entity.User;

public interface FlyInfoDao extends JpaRepository<FlyInfo, String>,FlyInfoDataDao{
	
	@Modifying
	@Query(value="update t_fly_info set departureflyno=?1,flightline=?2,incomingflyno=?3,planeno=?4,planetype=?5,estimatedfly=?6,task=?7,remark=?8 where incomingflyno=?3 and publishtime=?9",nativeQuery=true)
	void updateBaseFieldByIncomingFlyNo(String departureFlyNo,String flightLine,String incomingFlyNo,String planeNo,String planeType,Long estimatedFly,String task,String remark,long publishTime);
	
	@Modifying
	@Query(value="update t_fly_info set departureflyno=?1,flightline=?2,incomingflyno=?3,planeno=?4,planetype=?5,estimatedfly=?6,task=?7,remark=?8 where departureflyno=?1 and publishtime=?9",nativeQuery=true)
	void updateBaseFieldByDepartureFlyNo(String departureFlyNo,String flightLine,String incomingFlyNo,String planeNo,String planeType,Long estimatedFly,String task,String remark,long publishTime);
	
	@Query(value="select * from t_fly_info where time=?1",nativeQuery=true)
	List<FlyInfo> getFlyInfoListByTime(String time);
	
	@Query(value="select * from t_fly_info where publishtime=?1",nativeQuery=true)
	List<FlyInfo> getFlyInfoListByPublishTime(long publishTime);
	
	@Query(value="select max(publishtime) from t_fly_info",nativeQuery=true)
	long getLatestPublishTime();
}
