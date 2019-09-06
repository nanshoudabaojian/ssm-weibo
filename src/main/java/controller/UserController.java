package controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import bean.ResponseResult;
import bean.User;
import service.UserService;

/**
 * t_user控制器类
 * 
 * @author nanshoudabaojian
 *
 */
@RequestMapping("/user")
@Controller
public class UserController {

	@Resource
	private UserService userService;
	
	//异步提交登录
		@RequestMapping("/login.do")
		@ResponseBody
		public ResponseResult<Void> login(
				String username,String password,
				HttpSession session){
			//1.声明rr对象
			ResponseResult<Void> rr =  null;
			try{
				//2.调用业务层方法：login(username,password)返回user对象
				User user = 
						userService.login(
						username, password);
				//3.rr设置 state：1 message：登录成功
				rr = 
				new ResponseResult<Void>(1,"登录成功");
				//4.把user 对象绑定到session中
				session.setAttribute("user",user);
				}catch(RuntimeException ex){
					//5.rr设置 state：0 message：ex.getMessage();
					rr = 
					new ResponseResult<Void>(0,ex.getMessage());
				}
			return rr;
		}

	/**
	 * 异步提交注册
	 */
	@RequestMapping("/register.do")
	@ResponseBody
	public ResponseResult<Void> register(@RequestParam("inputUsername") String username,
			@RequestParam("inputPassword") String password, @RequestParam("inputEmail") String email,
			@RequestParam("inputPhone") String phone) {
		ResponseResult<Void> rr = null;
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		user.setEmail(email);
		user.setPhone(phone);
		try {
			userService.addUser(user);
			rr = new ResponseResult<Void>(1, "注册成功");
		} catch (RuntimeException e) {
			rr = new ResponseResult<Void>(0, e.getMessage());
		}
		return rr;
	}

	// 实现异步验证，用户名是否存在
	@RequestMapping("/checkUsername.do")
	@ResponseBody
	public ResponseResult<Void> checkUsername(String username) {
		// 1.声明ResponseResult<Void>对象
		ResponseResult<Void> rr = null;
		// 2.调用业务层方法
		User b = userService.selectByUsername(username);
		// 3.如果b为true；定义状态码 ： 0
		// 和状态信息:用户名不可以使用
		if (b != null) {
			rr = new ResponseResult<Void>(0, "用户名已存在");
		} else {
			// 4.如果b为false；定义状态码 ：1
			// 和状态信息:用户名可以使用
			rr = new ResponseResult<Void>(1, "用户名可以使用");
		}
		return rr;
	}

	// 验证邮箱的异步请求方法
	@RequestMapping("/checkEmail.do")
	@ResponseBody
	public ResponseResult<Void> checkEmail(String email) {
		// 1.声明rr对象
		ResponseResult<Void> rr = null;
		// 2.调用业务层方法：checkEmail(email);返回Boolean b
		// 3.如果b为true
		// 创建rr对象，state：0 message:邮箱不可以使用
		// 4.如果b为false
		// 创建rr对象，state：1 message:邮箱可以使用
		if (userService.selectByEmail(email) != null) {
			rr = new ResponseResult<Void>(0, "邮箱已被注册");
		} else {
			rr = new ResponseResult<Void>(1, "邮箱可以使用");
		}

		// 5.返回rr；
		return rr;
	}

	// 验证电话
	@RequestMapping("/checkPhone.do")
	@ResponseBody
	public ResponseResult<Void> checkPhone(String phone) {
		ResponseResult<Void> rr = null;
		if (userService.checkPhone(phone) != null) {
			rr = new ResponseResult<Void>(0, "该手机号已被注册");
		} else {
			rr = new ResponseResult<Void>(1, "该手机号可以使用");
		}
		return rr;
	}

	/**
	 * 显示注册的视图
	 */
	@RequestMapping("/showRegister.do")
	public String showRegister() {
		return "regist";
	}

	// 显示登录页面
	@RequestMapping("/showLogin.do")
	public String showLogin() {
		return "login";
	}
}
