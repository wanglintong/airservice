package cn.com.zlqf.airservice.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * 飞行动态表
 * @author Administrator
 *
 */
@Entity
@Table(name = "t_fly_dynamic")
public class FlyDynamic implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String id;
	/**
	 * 任务类型
	 */
	private String task;
	/**
	 * 机型
	 */
	private  String planeType;
	/**
	 * 机号
	 */
	private String planeNo;
	/**
	 * 进港航班号
	 */
	private String  incomingFlyNo;
	/**
	 * 出港航班号
	 */
	private String  departureFlyNo;
	/**
	 * 本站预计起飞
	 */
	private Long estimatedFly;
	/**
	 * 前站预计起飞
	 */
	private Long preEstimatedFly;
	/**
	 * 航线
	 */
	private String  flightLine;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 本条信息属于哪天  yyyy-MM-dd
	 */
	private String time;
	
	private String executionDate;
	/**
	 * 本条信息是哪天添加的 可能出现的情况是28号操作非28号的数据
	 */
	private Date addTime;
	/**
	 * 本条信息添加人员
	 */
	private String userId;
	/**
	 * 状态 0 未发布 1已发布
	 * @return
	 */
	private Integer state;
	
	/**
	 * 属性(国内|国内，地区|国际，地区|混合，国内)
	 */
	private String props;
	
	@Id
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTask() {
		return task;
	}
	public void setTask(String task) {
		this.task = task;
	}
	public String getPlaneType() {
		return planeType;
	}
	public void setPlaneType(String planeType) {
		this.planeType = planeType;
	}
	public String getPlaneNo() {
		return planeNo;
	}
	public void setPlaneNo(String planeNo) {
		this.planeNo = planeNo;
	}
	public String getIncomingFlyNo() {
		return incomingFlyNo;
	}
	public void setIncomingFlyNo(String incomingFlyNo) {
		this.incomingFlyNo = incomingFlyNo;
	}
	public String getDepartureFlyNo() {
		return departureFlyNo;
	}
	public void setDepartureFlyNo(String departureFlyNo) {
		this.departureFlyNo = departureFlyNo;
	}
	public String getFlightLine() {
		return flightLine;
	}
	public void setFlightLine(String flightLine) {
		this.flightLine = flightLine;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public Long getEstimatedFly() {
		return estimatedFly;
	}
	public void setEstimatedFly(Long estimatedFly) {
		this.estimatedFly = estimatedFly;
	}
	public Long getPreEstimatedFly() {
		return preEstimatedFly;
	}
	public void setPreEstimatedFly(Long preEstimatedFly) {
		this.preEstimatedFly = preEstimatedFly;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public Date getAddTime() {
		return addTime;
	}
	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}
	public String getExecutionDate() {
		return executionDate;
	}
	public void setExecutionDate(String executionDate) {
		this.executionDate = executionDate;
	}
	public String getProps() {
		return props;
	}
	public void setProps(String props) {
		this.props = props;
	}
	
}
