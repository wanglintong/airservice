package cn.com.zlqf.airservice.controller;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import cn.com.zlqf.airservice.entity.User;
import cn.com.zlqf.airservice.service.UserService;

@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@RequestMapping("/user/findUserById")
	public @ResponseBody User findUserById(HttpServletResponse response) {
		User user = userService.findUserById("1");
		return user;
	}
	
}
