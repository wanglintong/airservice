package cn.com.zlqf.airservice.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import cn.com.zlqf.airservice.utils.DateUtils;

/**
 * 航班信息 Created by ZLQFWL on 3/29 0029.
 */
@Entity
@Table(name = "t_fly_info")
public class FlyInfo extends BaseFly {

	private static final long serialVersionUID = 1L;
	private String id;
	/**表明这是哪天的航班信息  该值由FlyDynamic的time决定**/
	private String time;
	
	/**发布时间*/
	private Long publishTime;
	
	private Integer state;
	@Id
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Long getPublishTime() {
		return publishTime;
	}

	public void setPublishTime(Long publishTime) {
		this.publishTime = publishTime;
	}	
	
	
}
