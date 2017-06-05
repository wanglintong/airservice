package cn.com.zlqf.airservice.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="t_message")
public class Message implements Serializable{
	private String id;
	//存放原始报文
	private String message;
	//存放解析后的json报文
	private String jsonMessage;
	//报文装填 0 未审核 1 忽略  2已审核
	private Integer status;
	//string类型的时间 格式 yyyy-MM-dd 便于按照天查询报文
	private String time;
	//date类型的时间 便于按照报文添加时间进行排序
	private Date addTime;
	@Id
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	@Column(length=2000)
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
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
	
	public String getJsonMessage() {
		return jsonMessage;
	}
	public void setJsonMessage(String jsonMessage) {
		this.jsonMessage = jsonMessage;
	}
	
}
