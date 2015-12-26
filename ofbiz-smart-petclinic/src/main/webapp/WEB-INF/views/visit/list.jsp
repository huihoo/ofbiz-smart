<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="row" style="margin-top: 50px; ">
	
		
	<div class="col-lg-12 table-responsive" >
		<ol class="breadcrumb">
		  <li><a href="${ctxPath }/">首页</a></li>
		  <li class="active">宠物</li>
		</ol>
		
		<a href="${ctxPath }/pet/create${uriSuffix}"><i class="icon-plus"></i>&nbsp;新增</a>
		
		<table class="table table-hover table-bordered">
	      <thead>
	        <tr>
	          <th>#</th>
	          <th>名称</th>
	          <th>类型</th>
	          <th>主人</th>
	        </tr>
	      </thead>
	      <tbody>
	      	<c:forEach items="${list }" var="c">
	      	 	<tr>
		          <th scope="row">${c.id }</th>
		          <td>${c.name }</td>
		          <td>${c.type.name }</td>
		          <td>${c.owner.fistName }${c.owner.lastName }</td>
		        </tr>	
	      	</c:forEach>
	       
	      </tbody>
	    </table>
	</div>
</div>