package cn.com.zlqf.airservice.entity;

import java.io.Serializable;
import javax.persistence.MappedSuperclass;



/**
 * 航班信息
 * Created by ZLQFWL on 3/29 0029.
 */
@MappedSuperclass
public class BaseFly implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 属性(国内|国内，地区|国际，地区|混合，国内)
	 */
	private String props;
	//航班备注
	private String remark;
	
	/**
	 * 任务   正|正，正班，公务，备降、熟练、播种
	 */
	private String task;
	/**
	 * 进港航班号
	 */
	private String  incomingFlyNo;
	/**
	 * 出港航班号
	 */
	private String  departureFlyNo;
	/**
	 * 航线
	 */
	private  String  flightLine;
	/**
	 * 机型
	 */
	private  String planeType;
	/**
	 * 机号
	 */
	private String planeNo;
	/**
	 *前站实飞
	 */	
	private Long preRealFly;
	/**
	 * 进港状态	
	 */
	private String incomingProg;
	/**
	 * 预达(09:00)
	 */
	private Long  estimatedArrival;
	/**
	 * 计达(10:00)数据库将获取的字段转换为 bigint 数字
	 */
	private Long planedArrival;
	/**
	 * 实达(09:07) 本站实际降落时间 
	 */
	private Long realArrival;	
	 /**
     * 机位
     */
    private String location;
	/**
	 * 出港状态
	 * 
	 */
	private String departureProg;
	/**
	 * 登机口
	 */
	private String  boardingGate;
	/**
	 *计飞(10:30)
	 */	
	private Long planedFly;
	/**
	 *预飞
	 */	
	private Long  estimatedFly;
	/**
	 *本站实飞
	 */	
	private Long realFly;
	/**
	 * 进港异常  延误（异常原因：公司计划，流量控制）
	 */
	private String incomingExcep;
	/**
	 * 出港异常 延误（异常原因：流量控制，对方机场天气）
	 */
	private  String departureExcep;
	/**
	 * 行李转盘
	 */
	private String  baggageClaims;
	/**
	 * 柜台
	 */
	private String counter;
	/**
	 * 滑槽
	 */
	private String coulisse;
	/**
	 * 国内航站楼
	 */
	private String GuoneiTerminal;
	/**
	 * 国际航站楼
	 */
	private String GuojiTerminal;
	/**
	 * 开始值机
	 */
	private Long checkInStart;
	/**
	 * 值机截止
	 */
	private Long checkInStop;
	/**
	 * 开始登机
	 */
	private Long startBoarding;
	/**
	 * 登机结束
	 */
	private Long boardingEnd;
	/**
	 * 催促登机
	 */
	private Long urgingBoarding;
	/**
	 * 过站登机
	 */
	private Long boardingStation;
	/**
	 * 备降站
	 */ 
	private String alternate;
	/**
	 * 同步日期yyyyMMdd
	 */
	private Long executionDate;
	/**
	 * 进港执行日期yyyyMMdd
	 */
	private Long inExecuteDate;
	/**
	 * 出港执行日期yyyyMMdd
	 */
	private Long outExecuteDate;
	
	
	

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public String getProps() {
		return props;
	}

	public void setProps(String props) {
		this.props = props;
	}

	public Long getExecutionDate() {
		return executionDate;
	}

	public void setExecutionDate(Long executionDate) {
		this.executionDate = executionDate;
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

	public String getPlaneNo() {
		return planeNo;
	}

	public void setPlaneNo(String planeNo) {
		this.planeNo = planeNo;
	}

	public String getPlaneType() {
		return planeType;
	}

	public void setPlaneType(String planeType) {
		this.planeType = planeType;
	}

	public String getCounter() {
		return counter;
	}

	public void setCounter(String counter) {
		this.counter = counter;
	}

	public String getBoardingGate() {
		return boardingGate;
	}

	public void setBoardingGate(String boardingGate) {
		this.boardingGate = boardingGate;
	}

	public String getBaggageClaims() {
		return baggageClaims;
	}

	public void setBaggageClaims(String baggageClaims) {
		this.baggageClaims = baggageClaims;
	}

	public String getFlightLine() {
		return flightLine;
	}

	public void setFlightLine(String flightLine) {
		this.flightLine = flightLine;
	}


	public String getIncomingProg() {
		return incomingProg;
	}

	public void setIncomingProg(String incomingProg) {
		this.incomingProg = incomingProg;
	}

	public String getDepartureProg() {
		return departureProg;
	}

	public void setDepartureProg(String departureProg) {
		this.departureProg = departureProg;
	}

	public String getIncomingExcep() {
		return incomingExcep;
	}

	public void setIncomingExcep(String incomingExcep) {
		this.incomingExcep = incomingExcep;
	}

	public String getDepartureExcep() {
		return departureExcep;
	}
	public void setDepartureExcep(String departureExcep) {
		this.departureExcep = departureExcep;
	}
	public Long getPlanedArrival() {
		return planedArrival;
	}

	public void setPlanedArrival(Long planedArrival) {
		this.planedArrival = planedArrival;
	}

	public Long getEstimatedArrival() {
		return estimatedArrival;
	}

	public void setEstimatedArrival(Long estimatedArrival) {
		this.estimatedArrival = estimatedArrival;
	}

	public Long getRealArrival() {
		return realArrival;
	}

	public void setRealArrival(Long realArrival) {
		this.realArrival = realArrival;
	}

	public Long getPlanedFly() {
		return planedFly;
	}

	public void setPlanedFly(Long planedFly) {
		this.planedFly = planedFly;
	}

	public Long getEstimatedFly() {
		return estimatedFly;
	}

	public void setEstimatedFly(Long estimatedFly) {
		this.estimatedFly = estimatedFly;
	}

	public Long getRealFly() {
		return realFly;
	}

	public void setRealFly(Long realFly) {
		this.realFly = realFly;
	}

	public Long getPreRealFly() {
		return preRealFly;
	}

	public void setPreRealFly(Long preRealFly) {
		this.preRealFly = preRealFly;
	}

	public String getCoulisse() {
		return coulisse;
	}

	public void setCoulisse(String coulisse) {
		this.coulisse = coulisse;
	}

	public String getGuoneiTerminal() {
		return GuoneiTerminal;
	}

	public void setGuoneiTerminal(String guoneiTerminal) {
		GuoneiTerminal = guoneiTerminal;
	}

	public String getGuojiTerminal() {
		return GuojiTerminal;
	}

	public void setGuojiTerminal(String guojiTerminal) {
		GuojiTerminal = guojiTerminal;
	}

	public Long getCheckInStart() {
		return checkInStart;
	}

	public void setCheckInStart(Long checkInStart) {
		this.checkInStart = checkInStart;
	}

	public Long getCheckInStop() {
		return checkInStop;
	}

	public void setCheckInStop(Long checkInStop) {
		this.checkInStop = checkInStop;
	}

	public Long getStartBoarding() {
		return startBoarding;
	}

	public void setStartBoarding(Long startBoarding) {
		this.startBoarding = startBoarding;
	}

	public Long getBoardingEnd() {
		return boardingEnd;
	}

	public void setBoardingEnd(Long boardingEnd) {
		this.boardingEnd = boardingEnd;
	}

	public Long getUrgingBoarding() {
		return urgingBoarding;
	}

	public void setUrgingBoarding(Long urgingBoarding) {
		this.urgingBoarding = urgingBoarding;
	}

	public Long getBoardingStation() {
		return boardingStation;
	}

	public void setBoardingStation(Long boardingStation) {
		this.boardingStation = boardingStation;
	}

	public String getAlternate() {
		return alternate;
	}

	public void setAlternate(String alternate) {
		this.alternate = alternate;
	}

	public Long getInExecuteDate() {
		return inExecuteDate;
	}

	public void setInExecuteDate(Long inExecuteDate) {
		this.inExecuteDate = inExecuteDate;
	}

	public Long getOutExecuteDate() {
		return outExecuteDate;
	}

	public void setOutExecuteDate(Long outExecuteDate) {
		this.outExecuteDate = outExecuteDate;
	}
    
}
