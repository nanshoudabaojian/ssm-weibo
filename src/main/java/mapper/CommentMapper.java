package mapper;

import java.util.List;

import bean.Comment;

/**
 * t_comment
 * @author nanshoudabaojian
 *
 */
public interface CommentMapper {
	
	//发布评论
	boolean insertComment(Comment comment);
	
	//查询当前微博下的所有评论
	List<Comment> selectAll(Integer weiboId);
	
	//删除评论
	void deleteById(Integer commentId);
	
	//该条微博下有多少条评论
	Integer count(Integer weiboId);
}
