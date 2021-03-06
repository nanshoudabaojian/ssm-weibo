package test;

import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import bean.Comment;
import mapper.CommentMapper;
import service.ICommentService;

public class TestComment {
	/*
	 * 1.测试方法的返回类型为void类型
	 * 2.测试方法的访问修饰符为public
	 * 3.测试方法的参数列表为空
	 */
	
	@Test
	public void testInsert() {
		//1.
		AbstractApplicationContext ac = new ClassPathXmlApplicationContext("application-dao.xml");
		//2.
		CommentMapper mapper = ac.getBean("commentMapper", CommentMapper.class);
		//3.
		Comment comment = new Comment();
		comment.setUserId(23);
		comment.setWeiboId(12);
		comment.setCommentContent("测试评论1");
		mapper.insertComment(comment);
		//4.
		ac.close();
	}
	
	@Test
	public void testSelectAll() {
		//1.
		AbstractApplicationContext ac = new ClassPathXmlApplicationContext("application-dao.xml");
		//2.
		CommentMapper mapper = ac.getBean("commentMapper", CommentMapper.class);
		//3.
		Integer weiboId = 151;
		System.out.println(mapper.selectAll(weiboId));
		//4.
		ac.close();
	}
	
	@Test
	public void testSelectAlsl() {
		//1.
		AbstractApplicationContext ac = new ClassPathXmlApplicationContext("application-dao.xml");
		//2.
		CommentMapper mapper = ac.getBean("commentMapper", CommentMapper.class);
		//3.
		System.out.println(mapper.selectByComment(102));
		//4.
		ac.close();
	}
	
	@Test
	public void testSelectAlls() {
		//1.
		AbstractApplicationContext ac = new ClassPathXmlApplicationContext("application-dao.xml");
		//2.
		CommentMapper mapper = ac.getBean("commentMapper", CommentMapper.class);
		//3.
		System.out.println(mapper.selectAllComments(70));
		//4.
		ac.close();
	}
	@Test
	public void testSelectAllComment() {
		//1.
		AbstractApplicationContext ac = new ClassPathXmlApplicationContext("application-dao.xml");
		//2.
		CommentMapper mapper = ac.getBean("commentMapper", CommentMapper.class);
		//3.
		System.out.println(mapper.selectByUserId(70));
		//4.
		ac.close();
	}
	
	@Test
	public void testDel() {
		//1.
		AbstractApplicationContext ac = new ClassPathXmlApplicationContext("application-dao.xml");
		//2.
		CommentMapper mapper = ac.getBean("commentMapper", CommentMapper.class);
		//3.
		mapper.deleteById(1);
		//4.
		ac.close();
	}
	
	@Test
	public void testCount() {
		//1.
		AbstractApplicationContext ac = new ClassPathXmlApplicationContext("application-dao.xml");
		//2.
		CommentMapper mapper = ac.getBean("commentMapper", CommentMapper.class);
		//3.
		Integer count = mapper.count(88);
		System.out.println(count);
		//4.
		ac.close();
	}
	
	/**
	 * 测试业务层的insert
	 */
	@Test
	public void testAddComment() {
		AbstractApplicationContext ac = new ClassPathXmlApplicationContext("application-dao.xml","application-service.xml");
		ICommentService service = ac.getBean("commentService", ICommentService.class);
		Comment comment = new Comment();
		comment.setUserId(23);
		comment.setWeiboId(12);
		comment.setCommentContent("测试评论2");
		service.postComment(comment);
		ac.close();
	}
	
	@Test
	public void testSelect() {
		AbstractApplicationContext ac = new ClassPathXmlApplicationContext("application-dao.xml","application-service.xml");
		ICommentService service = ac.getBean("commentService", ICommentService.class);
		System.out.println(service.selectAll(12));
		ac.close();
	}
	
	
}
