package cn.com.zlqf.airservice.dao;

import com.alibaba.fastjson.JSONObject;

public interface FlyInfoDataDao {
	void updateMessageField(JSONObject jsonObject, Long time);
	void updateMessageFieldByFlyInfoId(JSONObject jsonObject, String id);
}
