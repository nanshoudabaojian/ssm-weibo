<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>我的首页</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<script src="../js/jquery-1.12.0.min.js"></script>
<script src="../js/bootstrap.min.js"></script>

<script type="text/javascript " src="../js/comment.js"></script>
<script type="text/javascript " src="../js/jquery.flexText.js "></script>

<!-- Bootstrap -->
<link rel="stylesheet" type="text/css" href="../css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="../css/login.css">


<link rel="stylesheet" href="../css/regist.css">
</head>
<body>

	<nav class="navbar navbar-default navbar-fixed-top">
		<div class="container">
			<p class="navbar-text navbar-right">
				<a href="#" class="navbar-link">实时</a>
			</p>
			<p class="navbar-text navbar-right">
				<a href="#" class="navbar-link">首页</a>
			</p>
			<p class="navbar-text navbar-right">
				<a href="#" class="navbar-link">好友圈</a>
			</p>
			<!-- 用户信息 -->
			<input type="hidden" value="${user.username }" id="username">
			<input type="hidden" value="${user.nickname }" id="nickname">
			<input type="hidden" value="${user}" id="userId">
			<div id="message"></div>
			<div class="container">
				<p class="navbar-text navbar-left">当前用户: ${user.username }</p>
				<p class="navbar-text navbar-left"><a href="../user/exit.do">退出登录</a></p>
			</div>
		</div>
	</nav>

	<div style="margin-top: 20px;">
		<form id="form-fabu" action="" method="post">
			<input name="userId" type="hidden" value="${user.id }"> 内容：<input
				name="content" type="text" /> <br /> 图1：<input name="pic1"
				type="file" /> <input name="original" type="hidden" value="1">
			<button type="button" id="fabu" value="提交">提交</button>
		</form>

		<div class="container" style="width: 850px; background-color: #fff;">
			<c:forEach items="${all}" var="weibo" varStatus="">
				<!-- 文字 -->
				<div style="text-align: left; margin-left: 85px">
					<p style="color: #333; font-size: 17px">${weibo.content }</p>
				</div>
				<br>
				<p style="color: #333">${weibo.postTime}</p>
				<!-- 原创微博 -->
				<!-- 图片 -->
				<div class="example" style="margin-left: 85px; margin-bottom: 20px;">
					<table>
						<tr>
							<c:if test="${weibo.pic1!=null }">
								<td><img src="/imgUpload/${weibo.pic1 }"
									style="width: 130px; height: 130px"></td>
							</c:if>
							<c:if test="${weibo.pic2!=null }">
								<td><img src="/imgUpload/${weibo.pic2 }"
									style="width: 130px; height: 130px"></td>
							</c:if>
							<c:if test="${weibo.pic3!=null }">
								<td><img src="/imgUpload/${weibo.pic3 }"
									style="width: 130px; height: 130px"></td>
							</c:if>
						</tr>
						<tr>
							<c:if test="${weibo.pic4!=null }">
								<td><img src="/imgUpload/${weibo.pic4 }"
									style="width: 130px; height: 130px"></td>
							</c:if>
							<c:if test="${weibo.pic5!=null }">
								<td><img src="/imgUpload/${weibo.pic5 }"
									style="width: 130px; height: 130px"></td>
							</c:if>
							<c:if test="${weibo.pic6!=null }">
								<td><img src="/imgUpload/${weibo.pic6 }"
									style="width: 130px; height: 130px"></td>
							</c:if>
						</tr>
						<tr>
							<c:if test="${weibo.pic7!=null }">
								<td><img src="/imgUpload/${weibo.pic7 }"
									style="width: 130px; height: 130px"></td>
							</c:if>
							<c:if test="${weibo.pic8!=null }">
								<td><img src="/imgUpload/${weibo.pic8 }"
									style="width: 130px; height: 130px"></td>
							</c:if>
							<c:if test="${weibo.pic9!=null }">
								<td><img src="/imgUpload/${weibo.pic9 }"
									style="width: 130px; height: 130px"></td>
							</c:if>
						</tr>
					</table>
				</div>

				<!-- 底部 -->
				<div class="container" style="width: 850px; background-color: #fff;">
					<br>
					<div class="btn-group" style="width: 100%">
						<button type="button" style="width: 25%" class="btn btn-success">收藏</button>
						<!-- Button trigger modal -->
						<button type="button" class="btn btn-primary " data-toggle="modal"
							data-target="#myModal" style="width: 25%"  data-id="${weibo.id }">转发</button>
						<button onclick="loadComment(${weibo.id})" style="width: 25%" class="btn btn-warning" id="bt">评论</button>
						<button type="button" style="width: 25%" class="btn btn-danger">点赞</button>
					</div>
					<br> <br>
				</div>

				<!-- 评论区 -->
		<div class="container "	style="width: 850px; background-color: #fff;">
			<div class="commentAll " id="com${weibo.id}" >
				<!--评论区域 begin-->
				<div class="reviewArea clearfix ">
				<form id="form-pinglun${weibo.id}">
					<input type="hidden" value="${user.id}" name="userId">
					<input type="hidden" value="${weibo.id}" name="weiboId">
					<textarea class="content comment-input " id="pinglunContent"
						placeholder="等待输入......." onkeyup="keyUP(this) " name="commentContent"></textarea>
					<a href="javascript:; " class="plBtn pinglun" id="pinglun">评论</a>
					<!-- 微博id -->
					<input type="hidden" value="${weibo.id}" class="weiboId">
				</form>
				</div>
				
				<!--评论区域 end-->
				<!--回复区域 begin-->
				<div class="comment-show " id="show${weibo.id }"></div>
				<!--回复区域 end-->
			</div>
			<hr>
		</div>


			</c:forEach>
		</div>


		<!-- bootstrap模态框（Modal） -->
		<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
			<div class="modal-dialog" role="document">
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal"
							aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
						<h4 class="modal-title" id="myModalLabel">转发微博</h4>
					</div>
					<div class="modal-body">
						<form id="form-zhuanfa">
							说点什么吧:<input type="text" class="form-inline" name="content" style="width: 500px"> 
							<input name="userId" type="hidden" value="${user.id }"> 
							<input id="repostId" name="repostId" type="hidden" value=""> <br /> 
							<input name="original" type="hidden" value="0">
						</form>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
						<button type="button" id="zhuanfa" class="btn btn-primary">发布</button>
					</div>
				</div>
			</div>
		</div>

	</div>

	<div></div>
	
<!-- //发布评论 -->
<script>

//<!--textarea限制字数-->
function keyUP(t) {
 var len = $(t).val().length;
 if (len > 139) {
     $(t).val($(t).val().substring(0, 140));
 }
}
$('.pinglun').click(function(){
	var id=$(this).parent().children("input")[2].value;
	console.log(id);
    	$.ajax({
    		url:"../weibo/postComment.do",
    		//serialize()表示提交表单所有组件
    		data:$("#form-pinglun" + id).serialize(),
    		type:"post",
    		dataType:"json",
    		success:function(obj){
    			
    		}
    	});
    	//window.location.href="../weibo/showOne.do";
  })
 
</script>

<!-- 加载评论 -->
<script>
function loadComment(weiboId){
    var commentAction = "../weibo/showComments.do?weiboId=" + weiboId;
    $.ajax({
        type: 'GET',
        url: commentAction,
        contentType: 'application/json;charset=utf-8',
        dataType: 'json',
        success:function(data) {
        	var result = data.data;
        	for(var i in result){
        		console.log(result[i].commentTime);
        		var date1 = new Date(result[i].commentTime);
        		var com = '<div class="comment-show-con clearfix ">' +
                '<div class="comment-show-con-img pull-left ">' +
              	//时间
                '<span class="date-dz-left  comment-time ">'+ date1.toLocaleString() +'</span> <br>' +
              	//昵称
                '<a href="# " class="comment-size-name ">'+ result[i].userId +'</a>' +
                //内容
                '<span class="my-pl-con ">:&nbsp; '+ result[i].commentContent +'</span>' +
                '<div class="date-dz ">'+
                
                '<div class="date-dz-right pull-right comment-pl-block ">' +
                //回复
                '<a href="javascript:; " class="date-dz-pl pl-hf hf-con-block pull-left ">回复</a>' +
                '<span class="pull-left date-dz-line ">|</span>' +
                '<a href="javascript:; " class="date-dz-z pull-left ">' +
                //赞
                '<i class="date-dz-z-click-red "></i>赞 (<i class="z-num ">666</i>)</a>' +
                '<span class="pull-left date-dz-line ">|</span>' +
                //删除
                '<a href="javascript:;" class="removeBlock " id="'+ result[i].commentId +'">删除</a>' +
                '</div></div><div class="hf-list-con " style="text-align: left;" id="123'+ result[i].commentId +'"></div>';
        		$('#show'+weiboId).prepend(com);
        	}
            
		}
    });
    
}

</script>

<script>
//删除当前行-评论
$(document).on('click','.removeBlock',function (e) {  
        console.log(e)      
        var id=$(this).attr('id');
        var url ="../weibo/removeById.do?weiboId="+id;
		 window.location.href=url;
    });
</script>


	<!-- //转发微博 -->
	<script>
	$('#myModal').on('show.bs.modal', function (event) {      
		var btnThis = $(event.relatedTarget); //触发事件的按钮      
		var modal = $(this);  //当前模态框      
		var modalId = btnThis.data('id');   //解析出data-id的内容      
		modal.find('#repostId').val(modalId);         
	});
	
		$('#zhuanfa').click(function() {
			$.ajax({
				url : "../weibo/post.do",
				//serialize()表示提交表单所有组件
				data : $("#form-zhuanfa").serialize(),
				type : "post",
				dataType : "json",
				success : function(obj) {}
			});
			window.location.href = "../weibo/showOne.do";
		})
	</script>
	<!-- //发布微博 -->
	<script>
$('#fabu').click(function(){
    	$.ajax({
    		url:"../weibo/post.do",
    		//serialize()表示提交表单所有组件
    		data:$("#form-fabu").serialize(),
    		type:"post",
    		dataType:"json",
    		success:function(obj){
    			
    		}
    	});
    	window.location.href="../weibo/showOne.do";
  })
 
</script>

</body>
</html>