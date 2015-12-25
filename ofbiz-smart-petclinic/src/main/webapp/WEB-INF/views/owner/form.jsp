<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div class="row" style="margin-top: 100px; ">
	<div class="col-lg-12" >
		<form action="${ctxPath }/owner/save${uriSuffix}" method="post">
		  <div class="form-group">
		    <label for="firstName">姓氏</label>
		    <input type="text" class="form-control input-lg" id="firstName" placeholder="请输入宠物主人的姓氏">
		  </div>
		  <div class="form-group">
		    <label for="lastName">名字</label>
		    <input type="lastName" class="form-control input-lg" id="lastName" placeholder="请输入宠物主人的名字">
		  </div>
		  <button type="submit" class="btn btn-default btn-lg">保存</button>
		</form>
	</div>
</div>