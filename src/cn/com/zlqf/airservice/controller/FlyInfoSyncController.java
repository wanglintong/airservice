package cn.com.zlqf.airservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.zlqf.airservice.entity.FlyInfo;
import cn.com.zlqf.airservice.service.FlyInfoService;

@Controller
public class FlyInfoSyncController {
	@Autowired
	private FlyInfoService flyInfoService;
	
	@RequestMapping("/flyInfoSync")
	public @ResponseBody List<FlyInfo> getFlyInfoList() {
		List<FlyInfo> flyInfoList = flyInfoService.getFlyInfoListFromRedis();
		return flyInfoList;
	}
}
