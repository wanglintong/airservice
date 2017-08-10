package cn.com.zlqf.airservice.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.zlqf.airservice.entity.FlyInfo;
import cn.com.zlqf.airservice.service.FlyInfoService;

@Controller
public class FlyInfoSyncController {
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	
	@Autowired
	private FlyInfoService flyInfoService;
	
	@RequestMapping("/flyInfoSync")
	public @ResponseBody List<FlyInfo> getFlyInfoList() {
		List<FlyInfo> flyInfoList = flyInfoService.getFlyInfoListFromRedis();
		/**  20170808格式的Long值  **/
		Long maxExecutionDate = 0l;
		for (FlyInfo flyInfo : flyInfoList) {
			if(flyInfo.getExecutionDate() > maxExecutionDate) {
				maxExecutionDate = flyInfo.getExecutionDate();
			}
		}
		for (FlyInfo flyInfo : flyInfoList) {
			flyInfo.setExecutionDate(maxExecutionDate);
		}
		return flyInfoList;
	}
}
