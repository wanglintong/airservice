package cn.com.zlqf.airservice.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.alibaba.fastjson.JSONObject;

import cn.com.zlqf.airservice.utils.SQLUtils;

public class FlyInfoDaoImpl implements FlyInfoDataDao{
	@PersistenceContext
	private EntityManager entityManager;
	@Override
	public void updateMessageField(JSONObject jsonObject) {
		String sql  = SQLUtils.createSQL(jsonObject);
		if(sql!=null) {
			Query query = entityManager.createNativeQuery(sql);
			query.executeUpdate();
		}
	}
	@Override
	public void updateMessageFieldBatch(String sqls) {
		if(sqls!=null) {
			Query query = entityManager.createNativeQuery(sqls);
			query.executeUpdate();
		}
	}
	
	@Override
	public void updateMessageFieldByFlyInfoId(JSONObject jsonObject, String id) {
		String sql  = SQLUtils.createSQLByFlyInfoId(jsonObject,id);
		if(sql!=null) {
			Query query = entityManager.createNativeQuery(sql);
			query.executeUpdate();
		}
	}
	
	@Override
	public void updateMessageFieldByFlyInfoIdBatch(String sqls) {
		if(sqls!=null) {
			Query query = entityManager.createNativeQuery(sqls);
			query.executeUpdate();
		}
	}
}
