<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div class="row" style="margin-top: 50px;">
	<div class="col-lg-12">
		<ol class="breadcrumb">
		  <li><a href="${ctxPath }/">首页</a></li>
		  <li class="active">查找宠物主人</li>
		</ol>
		
		<form class="form-horizontal well" action="${ctxPath }/owner/list${uriSuffix}">
		   <div class="input-group">
		      <input type="text" class="form-control input-lg" name="s_lastName_like" placeholder="输入宠物主人的姓名">
		      <span class="input-group-btn">
		        <button class="btn btn-default btn-lg" type="submit">查询</button>
		      </span>
		    </div><!-- /input-group -->
		</form>
	</div>
	
	<div class="col-lg-12">
		<a href="${ctxPath}/owner/create${uriSuffix}" class="btn btn-lg btn-default">
			<i class="icon-plus icon-large"></i>&nbsp;&nbsp;新增宠物主人
		</a> 
	</div>
	
</div>