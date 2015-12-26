<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="hh" uri="http://huihoo.com/tags" %>

<div class="row" style="margin-top: 50px; ">
	
		
	<div class="col-lg-12 table-responsive" >
		<ol class="breadcrumb">
		  <li><a href="${ctxPath }/">首页</a></li>
		  <li class="active">宠物主人</li>
		</ol>
		
		<a href="${ctxPath }/owner/create${uriSuffix}"><i class="icon-plus"></i>&nbsp;新增</a>
		
		<table class="table table-hover table-bordered">
	      <thead>
	        <tr>
	          <th>#</th>
	          <th>姓名</th>
	          <th>城市</th>
	          <th>地址</th>
	          <th>电话</th>
	          <th>宠物</th>
	        </tr>
	      </thead>
	      <tbody>
	      	<c:forEach items="${list }" var="c">
	      	 	<tr>
		          <th scope="row">${c.id }</th>
		          <td><a href="${ctxPath }/owner/view${uriSuffix}?id=${c.id}">${c.firstName }${c.lastName }</a></td>
		          <td>${c.city }</td>
		          <td>${c.address }</td>
		          <td>${c.telephone }</td>
		          <td>
		          	<c:forEach items="${c.pets }" var="p">
		          		${p.name }&nbsp;&nbsp;
		          	</c:forEach>
		          </td>
		        </tr>	
	      	</c:forEach>
	      </tbody>
	    </table>	    
	    <hh:pagination totalEntry="${totalEntry }" pageLink="" totalPage="${totalPage }"></hh:pagination>
	</div>
</div>