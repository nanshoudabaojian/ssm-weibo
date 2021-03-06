package controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import bean.ResponseResult;
import bean.User;
import bean.Weibo;
import service.IRelationService;
import service.UserService;
import service.WeiboService;

/**
 * t_user控制器类
 * 
 * @author nanshoudabaojian
 *
 */
@RequestMapping("/user")
@Controller
public class UserController {

	// 盐
	@Value("#{config.salt}")
	private String salt;

	// 日志log4j
	public final Logger log = Logger.getLogger(this.getClass());

	@Resource
	private UserService userService;

	@Resource
	private WeiboService weiboService;

	@Resource
	private IRelationService relationService;

	// 异步提交登录
	@RequestMapping("/login.do")
	@ResponseBody
	public ResponseResult<Void> login(String username, String password, HttpSession session) {
		// 1.声明rr对象
		ResponseResult<Void> rr = null;
		try {
			User user = userService.login(username, password);
			rr = new ResponseResult<Void>(1, "登录成功");
			session.setAttribute("user", user);
			// 当前用户的id
			Integer userId = user.getId();
			// 登录时就把用户拥有的微博数量给存入session中去
			Integer[] userIds = { userId };
			Integer countWeibo = weiboService.countMany(userIds);
			session.setAttribute("countWeibo", countWeibo);
			// 把粉丝数量也存进去
			Integer[] fans = relationService.selectFans(userId);
			Integer fanCount = fans.length;
			session.setAttribute("fanCount", fanCount);
			// 把关注数量也存进去
			Integer[] follows = relationService.selectAll(userId);
			Integer followCount = follows.length;
			session.setAttribute("followCount", followCount);
		} catch (RuntimeException ex) {
			rr = new ResponseResult<Void>(0, ex.getMessage());
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

	// 退出登录
	@RequestMapping("/exit.do")
	public String exit(HttpSession session) {
		// 设置session失效
		session.invalidate();
		return "redirect:../user/showLogin.do";
	}

	// 显示修改密码的页面
	@RequestMapping("/showPassword.do")
	public String showUpdatePassword(HttpSession session) {
		log.info("进入修改密码页面");
		return "password";
	}

	// 修改密码
	@RequestMapping("/updatePassword.do")
	@ResponseBody
	public ResponseResult<Void> undatePassword(HttpSession session, HttpServletRequest request,
			@RequestParam("oldpw") String oldpw, @RequestParam("newpw1") String newpw1,
			@RequestParam("newpw2") String newpw2) throws Exception {
		ResponseResult<Void> rr = null;
		int flag = 1;

		// 判断原密码是否一致
		User user = (User) session.getAttribute("user");
		log.info(user.getUsername() + "," + user.getPassword());
		try {
			userService.login(user.getUsername(), oldpw);
			rr = new ResponseResult<Void>(1, "正确");
		} catch (RuntimeException ex) {
			flag = 0;
			rr = new ResponseResult<Void>(0, "错误");
			request.setAttribute("error_old", "原密码不正确！");
		}
		if (flag == 0) {
			return rr;
		} else {
			User u = new User();
			u.setId(user.getId());
			u.setPassword(DigestUtils.md5Hex(newpw1 + salt));
			userService.updatePassword(u);
			return rr;
		}
	}

	// 显示修改个人资料页面
	@RequestMapping("/showUserinfo.do")
	public String showUpdate(HttpSession session) {
		log.info("进入修改页面");
		User user = (User) session.getAttribute("user");
		User newUser = userService.selectByUsername(user.getUsername());
		session.setAttribute("user", newUser);
		return "userinfo";
	}

	/**
	 * MultipartFile类专门用来接收前台传过来的文件
	 * @param user
	 * @param user_face
	 * @return
	 */
	// 实现修改用户个人信息
	@RequestMapping("/updateUserInfo.do")
	public String updateUser(User user, MultipartFile user_face) {
		// ————————————————————头像上传————————————————
		// //getOriginalFilename（）方法是得到原来的文件名在客户机的文件系统名称
		// 原始名称
		String originalFilename = user_face.getOriginalFilename();
		// 上传图片
		if (user_face != null && originalFilename != null && originalFilename.length() > 0) {

			// 存储图片的物理路径
			String pic_path = "D:\\Weibo_Person\\imgUpload\\";

			// 新的图片名称
			String newFileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
			// 新图片
			File newFile = new File(pic_path + newFileName);

			// 将内存中的数据写入磁盘
			try {
				// transferTo() 主要就是用来把 MultipartFile 转换成 File
				user_face.transferTo(newFile);
			} catch (IllegalStateException e) {
				e.printStackTrace();
				log.error("头像上传失败" + e);
			} catch (IOException e) {
				e.printStackTrace();
				log.error("头像上传失败" + e);
			}

			// 将新图片名称写到user中
			user.setFace(newFileName);
		}
		userService.updateUser(user);
		return "redirect:../user/showUserinfo.do";
	}

	// 前往我的微博主页
	@RequestMapping("/showOne.do")
	public String showById(ModelMap map, HttpServletRequest request, Integer page, Integer userId) {
		// 默认为当前页
		if (page == null) {
			page = 1;
		}
		// 一页展示10个 当前是第几个
		Integer offset = (page - 1) * 10;
		// 分页查询
		List<Weibo> all = weiboService.selectById(userId, offset, 10);
		// 存放用户
		User user = userService.selectById(userId);
		map.addAttribute("usernow", user);
		// 总微博数
		Integer count = weiboService.countByUser(userId);
		// 一页上显示10个，总共几页
		int pageSize = count % 10 == 0 ? count / 10 : count / 10 + 1;
		for (int i = 0; i < all.size(); i++) {
			
			/*
			 * 原创的悬浮信息
			 */
			userId = all.get(i).getUserId();
			// 把微博数量放进去
			Integer[] userIds = { userId };
			Integer countWeibo = weiboService.countMany(userIds);
			all.get(i).setWeibos(countWeibo);
			// 把粉丝数量也存进去
			Integer[] fans = relationService.selectFans(userId);
			Integer fanCount = fans.length;
			all.get(i).setFans(fanCount);
			// 把关注数量也存进去
			Integer[] follows = relationService.selectAll(userId);
			Integer followCount = follows.length;
			all.get(i).setFollows(followCount);
			
			// 是否原创
			Integer repostId = all.get(i).getRepostId();
			Weibo repost = weiboService.selectByWeiboId(repostId, offset, 10);
			
			/*
			 * 如果是非原创则将悬浮信息填充
			 */
			if(repost != null){
				userId = repost.getUserId();
				// 把微博数量放进去
				Integer[] userIds2 = { userId };
				countWeibo = weiboService.countMany(userIds2);
				repost.setWeibos(countWeibo);
				// 把粉丝数量也存进去
				fans = relationService.selectFans(userId);
				fanCount = fans.length;
				repost.setFans(fanCount);
				// 把关注数量也存进去
				follows = relationService.selectAll(userId);
				followCount = follows.length;
				repost.setFollows(followCount);	
			}
			all.get(i).setRepost(repost);
			log.info("展示单用户微博->非原创微博" + i + ":" + repost);
		}
		map.addAttribute("all", all);
		log.info(all);
		// 将页数和总数和当前页面放进session中
		map.addAttribute("count", count);
		map.addAttribute("pageSize", pageSize);
		map.addAttribute("curpage", page);
		map.addAttribute("wz", "showOne.do?userId=" + userId);
		// 查询当前用户的微博总数、粉丝总数、关注总数
		Integer nowWeiboCount = weiboService.countByUser(userId);
		Integer[] fans = relationService.selectFans(userId);
		Integer[] follows = relationService.selectAll(userId);
		// 将微博总数、粉丝总数、关注总数放入map中
		map.addAttribute("nowWeiboCount", nowWeiboCount);
		map.addAttribute("nowFansCount", fans.length);
		map.addAttribute("nowFollowCount", follows.length);
		return "myWeibo";
	}

}
