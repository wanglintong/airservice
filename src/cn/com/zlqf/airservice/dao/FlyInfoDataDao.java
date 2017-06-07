package cn.com.zlqf.airservice.dao;

import com.alibaba.fastjson.JSONObject;

public interface FlyInfoDataDao {
	void updateMessageField(JSONObject jsonObject);
	void updateMessageFieldByFlyInfoId(JSONObject jsonObject, String id);
	void updateMessageFieldBatch(String sqls);
	void updateMessageFieldByFlyInfoIdBatch(String sqls);
}
