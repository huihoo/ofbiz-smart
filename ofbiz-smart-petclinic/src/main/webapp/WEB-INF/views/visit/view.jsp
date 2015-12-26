<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<div class="row" style="margin-top: 50px; ">
	
	<div class="col-lg-12" >
		<ol class="breadcrumb">
		  <li><a href="${ctxPath }/">首页</a></li>
		  <li><a href="${ctxPath }/pet/list${uriSuffix}">宠物</a></li>
		  <li class="active">${model.name }</li>
		</ol>
		
		
	
		<table class="table table-hover table-bordered">
	      <tbody>
	      	<tr>
	      		<td width="40%"><strong>名字</strong></td>
	      		<td>${model.name}</td>
	      	</tr>
	      	<tr>
	      		<td><a href="${ctxPath }/owner/edit${uriSuffix}?id=${model.id}" class="btn btn-primary">
	      			<i class="icon-edit"></i> 编辑</a>
	      		</td>
	      	</tr>
	      </tbody>
	    </table>
	</div>
</div>