package cn.com.zlqf.airservice.controller;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSON;

import cn.com.zlqf.airservice.entity.FlyDynamic;
import cn.com.zlqf.airservice.entity.FlyInfo;
import cn.com.zlqf.airservice.entity.User;
import cn.com.zlqf.airservice.service.FlyDynamicService;
import cn.com.zlqf.airservice.service.FlyInfoService;
import cn.com.zlqf.airservice.utils.BaseUtils;
import cn.com.zlqf.airservice.utils.DateUtils;

@Controller
public class FlyDynamicController {
	
	@Autowired
	private FlyDynamicService flyDynamicService;
	@Autowired
	private FlyInfoService flyInfoService;
	
	private static Map<String,String> flyNoMap = BaseUtils.getFlyNoMap();
	
	@RequestMapping("/flyDynamic/getFlyDynamicList")
	public @ResponseBody List<FlyDynamic> getFlyDynamicList(String time) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if(time==null) {
			time = sdf.format(new Date());
		}
		return flyDynamicService.getListByTime(time);
	}
	
	@RequestMapping("/flyDynamic/updateFlyDynamic")
	public @ResponseBody Map<String,String> updateFlyDynamic(HttpServletRequest request,String flyInfoStr) throws Exception {
		Map<String,String> map = new HashMap<String,String>();
		FlyDynamic flyDynamic = JSON.parseObject(flyInfoStr,FlyDynamic.class);
		try {
			flyDynamicService.saveOrUpdate(flyDynamic);
			map.put("state", "ok");
		} catch (Exception e) {
			e.printStackTrace();
			map.put("state", "error");
		}
		return map;
	}
	
	@RequestMapping("/flyDynamic/delete")
	public @ResponseBody Map<String,String> delete(HttpServletRequest request,String idStr) throws Exception {
		Map<String,String> map = new HashMap<String,String>();
		String[] ids = idStr.split(",");
		try {
			flyDynamicService.deleteByIds(ids);
			map.put("state", "ok");
		} catch (Exception e) {
			e.printStackTrace();
			map.put("state", "error");
		}
		return map;
	}
	@RequestMapping("/flyDynamic/publishSingleUpdate")
	public @ResponseBody Map<String,String> publishSingleUpdate(HttpServletRequest request,String msg1,String msg2) throws Exception {
		Map<String,String> map = new HashMap<String,String>();
		try {
			//1需要更新   2直接插入
			List<FlyInfo> flyInfoList1 = JSON.parseArray(msg1, FlyInfo.class);
			List<FlyInfo> flyInfoList2 = JSON.parseArray(msg2, FlyInfo.class);
			
			//1是更新
			flyInfoService.updateBaseField(flyInfoList1);
			
			if(flyInfoList2!=null && flyInfoList2.size()>0) {
				//2直接插入
				long publishTime = flyInfoService.getLatestPublishTime();
				messageHandling(flyInfoList2, publishTime);
				flyInfoService.addFlyInfo(flyInfoList2);
			}
			
			//修改动态表状态
			List<FlyDynamic> flyDynamicList1 = JSON.parseArray(msg1, FlyDynamic.class);
			for(int i=0 ; flyDynamicList1!=null&&i<flyDynamicList1.size() ; ++i) {
				flyDynamicList1.get(i).setState(1);
			}
			flyDynamicService.saveOrUpdate(flyDynamicList1);
			
			List<FlyDynamic> flyDynamicList2 = JSON.parseArray(msg2, FlyDynamic.class);
			for(int i=0 ; flyDynamicList2!=null&&i<flyDynamicList2.size() ; ++i) {
				flyDynamicList2.get(i).setState(1);
			}
			flyDynamicService.saveOrUpdate(flyDynamicList2);
			String str = flyDynamicList1.size() + "条记录更新成功";
			if(flyDynamicList2!=null && flyDynamicList2.size()>0) {
				str = str + "," + flyDynamicList2.size() + "条记录添加成功";
			}
			map.put("retMsg", str);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("retMsg", "添加记录失败");
		}
		return map;
	}
	
	
	@RequestMapping("/flyDynamic/publishSingleAdd")
	public @ResponseBody Map<String,String> publishSingleAdd(HttpServletRequest request,String msg) throws Exception {
		Map<String,String> map = new HashMap<String,String>();
		try {
			List<FlyInfo> flyInfoList = JSON.parseArray(msg, FlyInfo.class);
			long publishTime = flyInfoService.getLatestPublishTime();
			messageHandling(flyInfoList, publishTime);
			
			flyInfoService.addFlyInfo(flyInfoList);
		
			List<FlyDynamic> flyDynamicList = JSON.parseArray(msg, FlyDynamic.class);
			for(int i=0 ; i<flyDynamicList.size() ; ++i) {
				flyDynamicList.get(i).setState(1);
			}
			flyDynamicService.saveOrUpdate(flyDynamicList);
			
			map.put("retMsg", flyInfoList.size() + "条记录添加成功");
		} catch (Exception e) {
			e.printStackTrace();
			map.put("retMsg", "添加记录失败");
		}
		return map;
	}
	
	
	@RequestMapping("/flyDynamic/checkIsPublished")
	public @ResponseBody Map<String,List<FlyInfo>> checkIsPublished(HttpServletRequest request,String msg) {
		Map<String,List<FlyInfo>> map = new HashMap<>();
		try {
			List<FlyInfo> retList = new ArrayList<>();
			List<FlyInfo> retList2 = new ArrayList<>(); 
			List<FlyInfo> flyInfoList = JSON.parseArray(msg, FlyInfo.class);
			List<FlyInfo> latestFlyInfoList = flyInfoService.getFlyInfoListFromRedis();
			
			for(FlyInfo flyInfo:flyInfoList) {
				String departureFlyNo = flyInfo.getDepartureFlyNo();
				String incomingFlyNo = flyInfo.getIncomingFlyNo();
				for(int i=0 ; latestFlyInfoList!=null&&i<=latestFlyInfoList.size() ; ++i) {
					if(i==latestFlyInfoList.size()) {
						retList2.add(flyInfo);
						break;
					}
					FlyInfo latestFlyInfo = latestFlyInfoList.get(i);
					if(latestFlyInfo.getDepartureFlyNo().equals(departureFlyNo) && !departureFlyNo.equals("")) {
						retList.add(flyInfo);
						break;
					}
					if(latestFlyInfo.getIncomingFlyNo().equals(incomingFlyNo) && !incomingFlyNo.equals("")) {
						retList.add(flyInfo);
						break;
					}
				}
			}
			map.put("retList", retList);
			map.put("retList2",retList2);
			map.put("retList3", flyInfoList);
			//无重复记录
			if(retList.size()==0) {
				long publishTime = flyInfoService.getLatestPublishTime();
				messageHandling(flyInfoList, publishTime);
				
				flyInfoService.addFlyInfo(flyInfoList);
			
				List<FlyDynamic> flyDynamicList = JSON.parseArray(msg, FlyDynamic.class);
				for(int i=0 ; i<flyDynamicList.size() ; ++i) {
					flyDynamicList.get(i).setState(1);
				}
				flyDynamicService.saveOrUpdate(flyDynamicList);
			}
			return map;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	//发布整表
	@RequestMapping("/flyDynamic/publishAll")
	public @ResponseBody Map<String,String> publishAll(HttpServletRequest request,String msg) throws Exception {
		Map<String,String> map = new HashMap<String,String>();
		//只要调用发布整表 则以最新的为准
		try {
			//前台传递过来的信息为flyDynamic，将其转变成对应的flyInfo信息
			List<FlyInfo> flyInfoList = JSON.parseArray(msg, FlyInfo.class);
			
			long publishTime = System.currentTimeMillis();
			messageHandling(flyInfoList,publishTime);
			
			flyInfoService.publishAll(flyInfoList);
			
			//将对应的flyDynamic信息变为已发布
			List<FlyDynamic> flyDynamicList = JSON.parseArray(msg, FlyDynamic.class);
			for(int i=0 ; i<flyDynamicList.size() ; ++i) {
				flyDynamicList.get(i).setState(1);
			}
			flyDynamicService.saveOrUpdate(flyDynamicList);
			
			map.put("state", "ok");
		} catch (Exception e) {
			e.printStackTrace();
			map.put("state", "error");
		}
		return map;
	}
	
	private void messageHandling(List<FlyInfo> flyInfoList,long publishTime) {
		for(int i=0 ; flyInfoList!=null && i<flyInfoList.size() ; ++i) {
			FlyInfo flyInfo = flyInfoList.get(i);
			
			flyInfo.setId(BaseUtils.getRandomId());
			
			String time = flyInfo.getTime();
			long parseLong = Long.parseLong(time.replace("-", ""));
			flyInfo.setExecutionDate(parseLong);
			flyInfo.setInExecuteDate(parseLong);
			flyInfo.setOutExecuteDate(parseLong);
			/*
			 * 将航班号由三字码转为二字码
			 */
			String oldDepartureFlyNo = flyInfo.getDepartureFlyNo();
			String oldIncomingFlyNo = flyInfo.getIncomingFlyNo();
			if(StringUtils.isNotBlank(oldDepartureFlyNo)) {
				String szm = oldDepartureFlyNo.substring(0, 3);
				String ezm = flyNoMap.get(szm);
				if(ezm!=null) {
					oldDepartureFlyNo = oldDepartureFlyNo.replace(szm, ezm);
				}
			}
			if(StringUtils.isNotBlank(oldIncomingFlyNo)) {
				String szm = oldIncomingFlyNo.substring(0, 3);
				String ezm = flyNoMap.get(szm);
				if(ezm!=null) {
					oldIncomingFlyNo = oldIncomingFlyNo.replace(szm, ezm);
				}
			}
			flyInfo.setDepartureFlyNo(oldDepartureFlyNo);
			flyInfo.setIncomingFlyNo(oldIncomingFlyNo);
			
			//设置发布时间
			flyInfo.setPublishTime(publishTime);
		}
	}
}
