package cn.com.zlqf.airservice.entity;

import javax.websocket.Session;

/**
 * 枚举常量
 * Created by molcotang on 2016/3/27.
 */

public class Const {
	
	//public static 
	
	public static Session session;//websocket发送给前台消息
	/**
	 * redis发布订阅监听channel
	 */
	public static final String MESSAGE_CHANNEL = "message_channel";
	/**
	 * 通用任务状态码 待接受
	 */
	public static final String TASKSTATUSCODE_DAIJIESHOU = "djs";
	/**
	 * 通用任务状态码 完成
	 */
	public static final String TASKSTATUSCODE_WANCHE = "wc";
	
	/**
	 * 通用任务状态码 任务撤销
	 */
	public static final String TASKSTATUSCODE_CANCEL = "cancel";
	
	/**
	 * 通用任务状态码 人车绑定
	 */
	public static final String TASKSTATUSCODE_RCBD = "rcbd";
	
	/**
	 * 任务状态变更
	 */
	public static final String MESSAGETYPE_TASKSTATUS = "taskStatus";
	/**
	 * 任务撤销
	*/
	public static final String MESSAGETYPE_TASKCANCEL = "taskCancel";
	/**
	 * 任务被手机端取消 通知调度员
	 */
	public static final String MESSAGETYPE_TASKRESET = "taskReset";
	/**
	 * 航班任务状态
	 */
	public static final String MESSAGETYPE_TASKFLYSTATUS = "taskFlyStatus";
	/**
	 * 航班信息变更
	 */
	public static final String MESSAGETYPE_FLYINFOUPDATE = "flyInfoUpdate";
	/**
	 * 航班预计到达
	 */
	public static final String MESSAGETYPE_FLYARRIVE = "flyArrive";
	/**
	 * 航班预计起飞
	 */
	public static final String MESSAGETYPE_FLYLEAVE = "flyLeave";
	/**
	 * 航班发布任务提醒
	 */
	public static final String MESSAGETYPE_PUBTASKREMIND= "pubTaskRemind";
	/**
	 * 超时任务提醒
	 */
	public static final String MESSAGETYPE_OVERTIMETASK= "overTimeTask";
	/**
	 * 告警信息
	 */
	public static final String MESSAGETYPE_ALARMINFO = "alarmInfo";
	/**
	 * 实时位置
	 */
	public static final String MESSAGETYPE_REALTIMELOCATION = "realTimeLocation";
	/**
	 * 消息通知
	 */
	public static final String MESSAGETYPE_NOTICEINFO = "noticeInfo";
	/**
	 * 人员车辆状态
	 */
	public static final String MESSAGETYPE_ONOFFLINE = "onOffLine";
	/**
	 * 人员移出未分组
	 */
	public static final String MESSAGETYPE_OUTUNGROUP = "outUnGroup";
	/**
	 * 人员移入未分组
	 */
	public static final String MESSAGETYPE_INUNGROUP = "inUnGroup";
	/**
	 *新增人员
	 */
	public static final String MESSAGETYPE_ADDUSER = "addUser";
	/**
	 * 新增车辆
	 */
	public static final String MESSAGETYPE_ADDVEHICLE = "addVehicle";
	/**
	 *删除人员
	 */
	public static final String MESSAGETYPE_DELUSER = "delUser";
	/**
	 * 删除车辆
	 */
	public static final String MESSAGETYPE_DELVEHICLE = "delVehicle";
	/**
	 * 新任务
	 */
	public static final String MESSAGETYPE_NEWTASK = "newTask";
	/**
	 * 基准站消息
	 */
	public static final String MESSAGETYPE_BASESTATION = "baseStation";
	/**
	 * 下线通知
	 */
	public static final String MESSAGETYPE_OFFLINENOTIFY = "offLineNotify";
	/**
	 * 未完成
	 */
	public static final String MESSAGETYPE_UNFINISHED = "unfinished";
	/**
	 * 未接受
	 */
	public static final String MESSAGETYPE_UNACCEPTED = "unaccepted";
	/**
	 *人员位置 
	 */
	public static final String MESSAGETYPE_WORKERLOCATION = "workerlocation";
	/**
	 * 检查同一用户（服务器接收 不下发）
	 */
	public static final String MESSAGETYPE_CHECKSAMEUSER = "checkSameUser";
	
	public static final String TASKTYPE_DAKETI = "daketi";
	public static final String TASKTYPE_CHEKETI = "cheketi";
	public static final Integer TASKREMIND_REDIS_EXPIRE = 60*60;
	public static final Long AIR_ADMIN_ID = 4l;

	/**
	 * 820人车绑定后将任务存入缓存，完成任务或撤销任务时清除
	 */
	public static final String REDIS_NAMESPACE_TASKBYHARDWAREID = "taskByHardwareId_";
	/**
	 * 820 刷卡后将驾驶员信息放入缓存
	 */
	public static final String REDIS_NAMESPACE_DRIVERBYHARDWAREID = "driverByHardwareId_";
	/**
	 * 进港，出港航班提醒完成
	 */
	public static final String MESSAGETYPE_REMINDFINISH_JG = "remindFinish_jg";
	public static final String MESSAGETYPE_REMINDFINISH_CG = "remindFinish_cg";
	
    public static enum VehicleType {

        FERRY("摆渡"),
        REPAIR("修理");

        private String label;
        private VehicleType(String label){
            this.label = label;
        }
    }

    public static enum AlarmType {

        SPEED("超速"),
        BORDER("越界");

        private String label;
        private AlarmType(String label){
            this.label = label;
        }
    }
    public static enum FenceType {
    	
    	PARKING("机位"),
    	DAOLUMIAN("道路面"),
    	CUSTOM("自定义围栏"),
    	OTHER("其他");
    	
    	private String label;
    	private FenceType(String label){
    		this.label = label;
    	}
    }
    public static enum FlightType {

        IN("进港"),
        OUT("出港");

        private String label;
        private FlightType(String label){
            this.label = label;
        }
    }
    public static enum TaskFlyStatus {
    	
    	PROGRESS("进行中"),
    	END("完成");
    	
    	private String label;
    	private TaskFlyStatus(String label){
    		this.label = label;
    	}
    }
    public static enum TaskFlyType {
    	
    	INCOMING("进港"),
    	DEPARTURE("出港");
    	
    	private String label;
    	private TaskFlyType(String label){
    		this.label = label;
    	}
    }
    public static enum UserStatus {
    	
    	ONLINE("在线"),
    	BUSY("忙碌"),
    	OFFLINE("离线"),
    	LEISURE("空闲");
    	private String label;
    	private UserStatus(String label){
    		this.label = label;
    	}
    }
    public static enum VehicleStatus {
    	
    	ONLINE("在线"),
    	BUSY("忙碌"),
    	ERROR("故障"),
    	OFFLINE("离线"),
    	LEISURE("空闲");
    	private String label;
    	private VehicleStatus(String label){
    		this.label = label;
    	}
    }
}