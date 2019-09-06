package mapper;

import java.util.List;

import bean.Weibo;

/**
 * t_weibo
 * 1.发布微博
 * @author nanshoudabaojian
 *
 */
public interface WeiboMapper {
	
	//发布微博
	boolean insert(Weibo weibo);
	
	//查询所有微博
	List<Weibo> selectAll();
	
}
