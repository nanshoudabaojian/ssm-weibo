package controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import bean.Comment;
import bean.Reply;
import bean.ResponseResult;
import bean.User;
import bean.Weibo;
import net.sf.json.JSONObject;
import service.ICommentService;
import service.IRelationService;
import service.IReplyService;
import service.UserService;
import service.WeiboService;

/**
 * 微博控制器层
 * 
 * @author nanshoudabaojian
 *
 */
@RequestMapping("/weibo")
@Controller
public class WeiboController {

	// 日志log4j
	public final Logger log = Logger.getLogger(this.getClass());
	@Resource
	private WeiboService weiboService;// 微博

	@Resource
	private UserService userService;// 用户
	
	@Resource
	private IReplyService replyService;// 回复
	
	@Resource
	private ICommentService commentService;// 评论
	
	@Resource
	private IRelationService relationService;// 粉丝关注

	// 转发微博
	@RequestMapping("/repost.do")
	@ResponseBody
	public ResponseResult<Weibo> post(Weibo weibo) {
		ResponseResult<Weibo> rr = new ResponseResult<Weibo>(1, "转发成功");
		weiboService.insertWeibo(weibo);
		return rr;
	}

	// 发布微博
	@RequestMapping("/post.do")
	// 将form表单中的数据封装到weibo对象中
	// 直接利用form的submit->然后重定向到我的首页showOne.do
	public String post(HttpServletRequest request, HttpSession session) {
		Weibo weibo = new Weibo();
		String content = request.getParameter("content");
		int count = 0;
		for (int i = 1; i < 30; i++) {
			// 每上传一张图片，就会动态生成一个隐藏域->里面存放着文件的uuid(name)
			String pic = request.getParameter("pic_pic_" + i);
			if (pic != null) {
				count++;
				switch (count) {
				case 1:
					weibo.setPic1(pic);
					break;
				case 2:
					weibo.setPic2(pic);
					break;
				case 3:
					weibo.setPic3(pic);
					break;
				case 4:
					weibo.setPic4(pic);
					break;
				case 5:
					weibo.setPic5(pic);
					break;
				case 6:
					weibo.setPic6(pic);
					break;
				case 7:
					weibo.setPic7(pic);
					break;
				case 8:
					weibo.setPic8(pic);
					break;
				case 9:
					weibo.setPic9(pic);
					break;
				}
			}
		}

		// 用户id content 原创
		User user = (User) session.getAttribute("user");
		weibo.setUserId(user.getId());
		weibo.setContent(content);
		weibo.setOriginal(1);

		//发布一条微博 微博总数+1
		session.setAttribute("countWeibo", (Integer)session.getAttribute("countWeibo") + 1);
		// 执行发送
		weiboService.insertWeibo(weibo);
		return "redirect:showOne.do";
	}

	// 展示所有微博
	@RequestMapping("/show.do")
	public String showAll(ModelMap map, Integer page) {
		if (page == null) {
			page = 1;
		}
		Integer offset = (page - 1) * 10;
		List<Weibo> all = weiboService.selectAll(offset, 10);
		Integer count = weiboService.count();
		int pageSize = count % 10 == 0 ? count / 10 : count / 10 + 1;
		for (int i = 0; i < all.size(); i++) {
			
			/*
			 * 原创用户的悬浮信息
			 */
			int userId = all.get(i).getUserId();
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
			
			/*
			 * 非原创用户的悬浮信息
			 */
			Integer repostId = all.get(i).getRepostId();
			Weibo repost = weiboService.selectByWeiboId(repostId, 0, 10);
			
			//如果是非原创则将悬浮信息填充
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
			
			// 将非原创用户添加进行
			all.get(i).setRepost(repost);
			
		}
		
		map.addAttribute("count", count);
		map.addAttribute("pageSize", pageSize);
		map.addAttribute("all", all);
		map.addAttribute("curpage", page);
		map.addAttribute("wz", "show.do?");
		return "now";
	}

	// 显示指定用户的微博->生硬的从session中取出了user对象进而取出了id
	@RequestMapping("/showOne.do")
	public String showById(ModelMap map, HttpServletRequest request, Integer page) {
		// 默认为当前页
		if (page == null) {
			page = 1;
		}
		// 一页展示10个 当前是第几个
		Integer offset = (page - 1) * 10;
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		Integer userId = user.getId();
		// 分页查询
		List<Weibo> all = weiboService.selectById(userId, offset, 10);
		// 总微博数
		Integer count = weiboService.countByUser(userId);
		// 一页上显示10个，总共几页
		int pageSize = count % 10 == 0 ? count / 10 : count / 10 + 1;
		for (int i = 0; i < all.size(); i++) {
			
			/*
			 * 原创微博的悬浮信息
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
			Weibo repost = weiboService.selectByWeiboId(repostId, 0, 10);
			
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
		}
		map.addAttribute("all", all);
		// 将页数和总数和当前页面放进session中
		map.addAttribute("count", count);
		map.addAttribute("pageSize", pageSize);
		map.addAttribute("curpage", page);
		map.addAttribute("wz", "showOne.do?");
		return "mine";
	}
	
	// 显示指定微博的全部内容
	@RequestMapping("/showSingle.do")
	public String showSingle(ModelMap map, Integer weiboId) {
		log.info("展示指定人的主页");
		//查出该条微博的全部内容
		Weibo weibo = weiboService.selectByWeiboId(weiboId, 0, 1);
		
		// 是否原创 ->并设置非原创微博的全部信息
		Integer repostId = weibo.getRepostId();
		Weibo repost = weiboService.selectByWeiboId(repostId, 0, 10);
		weibo.setRepost(repost);
		
		//查出该条微博所有的评论
		List<Comment> comment = commentService.selectAll(weiboId);
		//存储所有的评论
		List<Comment> commentList = new ArrayList<Comment>();
		
		for(int i=0; i<comment.size(); i++){
			Comment c = comment.get(i);
			Integer id = c.getCommentId();
			List<Reply> replys = replyService.selectAllReply(id);
			//存储评论下的回复
			List<Reply> replyList = new ArrayList<Reply>();
			for(int j=0; j< replys.size(); j++){
				replyList.add(replys.get(j));
			}
			//将该条评论下的所有回复都放入
			c.setReply(replyList);
			//再将这条评论放入
			commentList.add(c);
		}
		map.addAttribute("commentList", commentList);
		log.info("展示单条微博" + weiboId);
		
		map.addAttribute("weibo", weibo);
		return "single";
	}

	// 显示指定用户的指定微博
	@RequestMapping("/selectByContent.do")
	public String showByContent(Integer page, ModelMap map, HttpServletRequest request,
			@RequestParam("userId") Integer userId, @RequestParam("content") String content) {
		log.info("用户:" + userId + "，要搜的内容是:" + content + ",当前页数:" + page);
		// 默认为当前页
		if (page == null) {
			page = 1;
		}
		// 一页展示10个 当前是第几个
		Integer offset = (page - 1) * 10;
		// 有多少个符合条件的查询结果
		Integer count = weiboService.selectByContentNoPage(userId, content);

		List<Weibo> all = weiboService.selectByContent(userId, content, offset, count);

		// 一页上显示10个，总共几页
		int pageSize = count % 10 == 0 ? count / 10 : count / 10 + 1;
		// 存放所有转发的微博
		for (int i = offset; i < pageSize; i++) {
			// 是否原创
			Integer repostId = all.get(i).getRepostId();
			Weibo repost = weiboService.selectByWeiboId(repostId, 0, 10);
			all.get(i).setRepost(repost);
		}
		map.addAttribute("all", all);
		// 将页数和总数和当前页面放进session中
		map.addAttribute("count", count);
		map.addAttribute("pageSize", pageSize);
		map.addAttribute("curpage", page);
		map.addAttribute("wz", "selectByContent.do?userId=" + userId + "&content=" + content + "&");
		return "mine";
	}

	@RequestMapping("/delWeibo.do")
	public String delWeibo(Integer id, HttpSession session) {
		weiboService.delWeibo(id);
		log.info("删除当前微博成功,id=" + id);
		//删除一条微博 微博总数-1
		session.setAttribute("countWeibo", (Integer)session.getAttribute("countWeibo") - 1);
		return "redirect:showOne.do";
	}

	/**
	 * 文件(图片)上传
	 * JSONObject用于解析json对象
	 * UUID通用唯一识别码->确定唯一性
	 * @param file
	 * @param model
	 * @param session
	 * @return
	 * @throws Exception
	 */
	// 图片上传
	@RequestMapping("/upload.do")
	public @ResponseBody String upload(MultipartFile file, Model model, HttpSession session) throws Exception {
		log.info("图片上传开始");
		JSONObject json = new JSONObject();
		// 原始名称
		String originalFilename = file.getOriginalFilename();
		// 上传图片
		if (file != null && originalFilename != null && originalFilename.length() > 0) {

			// 存储图片的物理路径
			String pic_path = "D:\\Weibo_Person\\imgUpload\\";

			// 新的图片名称
			String newFileName = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
			String pic_1, pic_2, pic_3, pic_4, pic_5, pic_6, pic_7, pic_8, pic_9;
			pic_1 = (String) session.getAttribute("pic_1");
			pic_2 = (String) session.getAttribute("pic_2");
			pic_3 = (String) session.getAttribute("pic_3");
			pic_3 = (String) session.getAttribute("pic_3");
			pic_4 = (String) session.getAttribute("pic_4");
			pic_5 = (String) session.getAttribute("pic_5");
			pic_6 = (String) session.getAttribute("pic_6");
			pic_7 = (String) session.getAttribute("pic_7");
			pic_8 = (String) session.getAttribute("pic_8");
			pic_9 = (String) session.getAttribute("pic_9");
			if (pic_1 == null) {
				pic_1 = newFileName;
				session.setAttribute("pic_1", newFileName);
			} else if (pic_2 == null) {
				pic_2 = newFileName;
				session.setAttribute("pic_2", newFileName);
			} else if (pic_3 == null) {
				pic_3 = newFileName;
				session.setAttribute("pic_3", newFileName);
			} else if (pic_4 == null) {
				pic_3 = newFileName;
				session.setAttribute("pic_4", newFileName);
			} else if (pic_5 == null) {
				pic_3 = newFileName;
				session.setAttribute("pic_5", newFileName);
			} else if (pic_6 == null) {
				pic_3 = newFileName;
				session.setAttribute("pic_6", newFileName);
			} else if (pic_7 == null) {
				pic_3 = newFileName;
				session.setAttribute("pic_7", newFileName);
			} else if (pic_8 == null) {
				pic_3 = newFileName;
				session.setAttribute("pic_8", newFileName);
			} else if (pic_9 == null) {
				pic_3 = newFileName;
				session.setAttribute("pic_9", newFileName);
			}
			// 新图片
			File newFile = new File(pic_path + newFileName);

			// 将内存中的数据写入磁盘
			file.transferTo(newFile);

			// 将新图片名称写到user中

			json.put("status", "OK");
			json.put("picName", newFileName);
			return json.toString();
		}
		json.put("status", "NO");
		log.info("图片上传结束");
		return json.toString();
	}
	
}
