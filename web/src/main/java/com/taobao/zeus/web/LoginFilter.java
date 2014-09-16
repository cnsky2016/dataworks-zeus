package com.taobao.zeus.web;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.taobao.zeus.store.UserManager;
import com.taobao.zeus.store.mysql.persistence.ZeusUser;
/**
 * 登陆信息设置
 * @author zhoufang
 *
 */
public class LoginFilter implements Filter {
	
	
	private UserManager userManager;
	private SSOLogin login=new SSOLogin() {
/*<<<<<<< HEAD
//		private String username = "";
//		private String usermail = "";
//		private String fullname = "";
//		private String phone = "";
//		private String department = "";
		public String getUid(HttpServletRequest request) {
//			AttributePrincipal principal = (AttributePrincipal)request.getUserPrincipal();
//			Map<String,String> map = principal.getAttributes();
//			username = map.get("name");
//			usermail = map.get("mail");
//			department = map.get("department");
//			fullname = map.get("sn");
//			return username;
			return ZeusUser.ADMIN.getUid();

		}
		public String getPhone(HttpServletRequest req) {
			return ZeusUser.ADMIN.getPhone();
			//return phone;
		}
		public String getName(HttpServletRequest req) {
			return ZeusUser.ADMIN.getName();
			//return fullname;
		}
		public String getEmail(HttpServletRequest req) {
			return ZeusUser.ADMIN.getEmail();
			//return usermail;
=======*/
		public String getUid(HttpServletRequest req) {
		
			return ZeusUser.USER.getUid();
		}
		public String getPhone(HttpServletRequest req) {
			return ZeusUser.USER.getPhone();
		}
		public String getName(HttpServletRequest req) {
			return ZeusUser.USER.getName();
		}
		public String getEmail(HttpServletRequest req) {
			return ZeusUser.USER.getEmail();
		}
	};
	@Override
	public void destroy() {
		// do nothing
	}
	
	
	public interface SSOLogin{
		String getUid(HttpServletRequest req);
		String getEmail(HttpServletRequest req);
		String getName(HttpServletRequest req);
		String getPhone(HttpServletRequest req);
	}
	

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest httpRequest=(HttpServletRequest) request;
		HttpServletResponse httpResponse=(HttpServletResponse)response;
		httpResponse.setCharacterEncoding("utf-8");
		//线上服务器检测
		if(httpRequest.getRequestURI().equals("/zeus.check")){
			response.getWriter().write("success");
			return;
		}
	
		ZeusUser zeusUser=null;
		String uri=httpRequest.getRequestURI();
/*		if(uri.contains("zeus_platform.nocache.js")){
			System.out.println(uri);
		}*/
		if(uri.endsWith(".taobao") || uri.endsWith(".js") || uri.endsWith(".css") || uri.endsWith(".gif") ||
				uri.endsWith(".jpg") || uri.endsWith(".png") || uri.endsWith("dump.do")){
			chain.doFilter(request, response);
			return;
		}
	
		String uid=login.getUid(httpRequest);
		if(uid==null){
			return;
		}
		System.out.println("1`3333");
		zeusUser=new ZeusUser();
		zeusUser.setEmail(login.getEmail(httpRequest));
		zeusUser.setUid(login.getUid(httpRequest));
		zeusUser.setName(login.getName(httpRequest));
		zeusUser.setPhone(login.getPhone(httpRequest));
		if(!uid.equals(httpRequest.getSession().getAttribute("user"))){
			userManager.addOrUpdateUser(zeusUser);
			httpRequest.getSession().setAttribute("user", zeusUser.getUid());
		}
		LoginUser.user.set(zeusUser);
		//System.out.println(zeusUser.toString());
		
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		ApplicationContext applicationContext=WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
		userManager=(UserManager) applicationContext.getBean("userManager");
		if(applicationContext.containsBean("ssoLogin")){
			login=(SSOLogin)applicationContext.getBean("ssologin");
		}
	}

}
