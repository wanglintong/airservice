package cn.com.zlqf.airservice.controller;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

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
public class LoginController {
	
	@Autowired
	private UserService userService;
	
	@RequestMapping("/login/toLogin")
	public String toLogin(HttpServletRequest request) throws Exception {
		User token = (User) request.getSession().getAttribute("user");
		if(token!=null) {
			return "index";
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
					return "index";
				}
			}
		}
		return "login";
	}
		
	@RequestMapping("/login/login")
	public String login(HttpServletRequest request,HttpServletResponse response,String username,String password,boolean rememberUser) throws Exception {
		if(request.getSession().getAttribute("user")!=null) {
			return "index";
		}
		User user = userService.findUserByUsernameAndPassword(username, password);
		if(user==null) {
			request.setAttribute("msg", "用户名或密码错误");
			return "login";
		}else {
			if(rememberUser) {
				Cookie cookie = new Cookie("userInfo", URLEncoder.encode(username,"UTF-8")+","+password);
				//设置默认有效期为一周
				cookie.setMaxAge(60*60*24*7);
				cookie.setPath("/airservice");
				response.addCookie(cookie);
			}
			request.getSession().setAttribute("user", user);
			return "index";
		}
	}
	
	@RequestMapping("/login/logout")
	public String logout(HttpServletRequest request,HttpServletResponse response) {
		request.getSession().removeAttribute("user");
		Cookie cookie = new Cookie("userInfo","");
		cookie.setMaxAge(0);
		cookie.setPath("/airservice");
		response.addCookie(cookie);
		return "login";
	}

}
