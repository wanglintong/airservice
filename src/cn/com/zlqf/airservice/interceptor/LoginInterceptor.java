package cn.com.zlqf.airservice.interceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.com.zlqf.airservice.entity.User;
import cn.com.zlqf.airservice.service.UserService;

public class LoginInterceptor implements HandlerInterceptor{
	
	@Autowired
	private UserService userService;
	
	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object obj) throws Exception {
		User user = (User) request.getSession().getAttribute("user");
		String contextPath = request.getContextPath();
		if(user==null) {
			Cookie[] cookies = request.getCookies();
			for(int i=0 ; cookies!=null&&i<cookies.length ; ++i) {
				if(cookies[i].getName().equals("userInfo")){
					String userInfo = cookies[i].getValue();
					String username = userInfo.split(",")[0];
					String password = userInfo.split(",")[1];
					user = userService.findUserByUsernameAndPassword(username, password);
					if(user==null) {
						//用户更改过了密码 重新登录
						if(request.getRequestURI().equals(request.getContextPath()+"/login/toLogin.do")){
							return true;
						}
						
						response.sendRedirect(contextPath+"/login/toLogin.do");
						return false;
					}else {
						request.getSession().setAttribute("user", user);
						return true;
					}
				}
			}
			//没有cookie信息 跳转到登录页面
			if(request.getRequestURI().equals(request.getContextPath()+"/login/toLogin.do")){
				return true;
			}
			
			response.sendRedirect(contextPath+"/login/toLogin.do");
			return false;
		}else {
			//用户已登录 放行
			return true;
		}
	}
}
