<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div class="row" style="margin-top: 50px; ">
	
	<div class="col-lg-12" >
		<ol class="breadcrumb">
		  <li><a href="${ctxPath }/">首页</a></li>
		  <li><a href="${ctxPath }/list${uriSuffix}">宠物主人</a></li>
		  <li class="active">${model.firstName }</li>
		</ol>
	
		<table class="table table-hover table-bordered">
	      <tbody>
	      	<tr>
	      		<td width="40%"><strong>姓名</strong></td>
	      		<td>${model.firstName }${model.lastName }</td>
	      	</tr>
	        <tr>
	      		<td><strong>城市</strong></td>
	      		<td>${model.city }</td>
	      	</tr>
	      	<tr>
	      		<td><strong>地址</strong></td>
	      		<td>${model.address }</td>
	      	</tr>
	      	<tr>
	      		<td><strong>电话</strong></td>
	      		<td>${model.telephone }</td>
	      	</tr>
	      	<tr>
	      		<td><a href="${ctxPath }/owner/edit${uriSuffix}?id=${model.id}" class="btn btn-primary">
	      			<i class="icon-edit"></i> 编辑</a>
	      		</td>
	      		<td><a href="${ctxPath }/pet/create${uriSuffix}?ownerId=${model.id}" class="btn btn-success">
	      			<i class="icon-plus"></i> 新增宠物</a>
	      		</td>
	      	</tr>
	      </tbody>
	    </table>
	</div>
</div>