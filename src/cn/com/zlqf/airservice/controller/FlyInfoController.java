package cn.com.zlqf.airservice.controller;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSON;
import cn.com.zlqf.airservice.entity.FlyInfo;
import cn.com.zlqf.airservice.entity.User;
import cn.com.zlqf.airservice.service.UserService;

@Controller
public class FlyInfoController {
	
	@Autowired
	private UserService userService;
	
	@RequestMapping("/flyinfo/getFlyInfoList")
	public @ResponseBody List<FlyInfo> getFlyInfoList() {
		List<FlyInfo> flyInfoList = new ArrayList<>();
		for(int i=0;i<25;++i) {
			FlyInfo flyInfo = new FlyInfo();
			flyInfo.setId(i+"");
			flyInfo.setTask("正班");
			flyInfo.setPlaneType("测试机型"+i);
			flyInfo.setPlaneNo("机号00" + i);
			flyInfo.setIncomingFlyNo("JG"+i);
			flyInfo.setDepartureFlyNo("CG"+i);
			flyInfo.setFlightLine("北京-洛杉矶-休斯顿-纽约-华盛顿");
			flyInfo.setRemark("测试航班信息");
			flyInfo.setState(i);
			flyInfoList.add(flyInfo);
		}
		
		return flyInfoList;
	}
	
	@RequestMapping("/flyinfo/updateFlyInfo")
	public @ResponseBody Map<String,String> updateFlyInfo(String flyInfoStr) {
		Map<String,String> map = new HashMap<String,String>();
		System.out.println(flyInfoStr);
		FlyInfo flyInfo = JSON.parseObject(flyInfoStr, FlyInfo.class);
		System.out.println(flyInfo);
		map.put("state", "OK");
		return map;
	}
	@RequestMapping("/flyinfo/toFlyInfoCheck")
	public String toFlyInfoCheck(HttpServletRequest request) throws Exception {
		User token = (User) request.getSession().getAttribute("user");
		if(token!=null) {
			return "check";
		}
		Cookie[] cookies = request.getCookies();
		for(int i=0 ; cookies!=null&&i<cookies.length ; ++i) {
			if(cookies[i].getName().equals("userInfo")){
				String userInfo = cookies[i].getValue();
				String username = URLDecoder.decode(userInfo.split(",")[0],"UTF-8");
				String password = userInfo.split(",")[1];
				User user = userService.findUserByUsernameAndPassword(username, password);
				if(user!=null) {
					request.getSession().setAttribute("user", user);
					return "check";
				}
			}
		}
		return "login";
	}
	
}
