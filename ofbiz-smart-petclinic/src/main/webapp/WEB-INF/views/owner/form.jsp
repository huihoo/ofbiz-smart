<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="row"  style="margin-top: 50px;">
	<div class="col-lg-12" >
		<ol class="breadcrumb">
		  <li><a href="${ctxPath }/">首页</a></li>
		  <li><a href="${ctxPath }/owner/list${uriSuffix}">宠物主人</a></li>
		  <li class="active">
		  	<c:choose>
		  		<c:when test="${not empty(model) }">${model.firstName }${model.lastName }</c:when>
		  		<c:otherwise>新增宠物主人</c:otherwise>
		  	</c:choose>
		  </li>
		</ol>
		
		<c:choose>
	  		<c:when test="${not empty(model) }">
	  			<c:set var="action" value="${ctxPath }/owner/update${uriSuffix}"></c:set>
	  		</c:when>
	  		<c:otherwise>
	  			<c:set var="action" value="${ctxPath }/owner/save${uriSuffix}"></c:set>
	  		</c:otherwise>
	  	</c:choose>
		  	
		
		<form action="${action}" method="post">
		  <input type="hidden" name="id" value="${model.id }"/>
		  <div class="form-group">
		    <label for="firstName">姓氏</label>
		    <input type="text" class="form-control input-lg"  name="firstName" id="firstName" placeholder="请输入您的姓氏" value="${model.firstName }">
		  </div>
		  <div class="form-group">
		    <label for="lastName">名字</label>
		    <input type="text" class="form-control input-lg" name="lastName"  id="lastName" placeholder="请输入您名字" value="${model.lastName }">
		  </div>
		   <div class="form-group">
		    <label for="city">城市</label>
		    <input type="text" class="form-control input-lg" name="city"  id="city" placeholder="请输入您所在的城市" value="${model.city }">
		  </div>
		   <div class="form-group">
		    <label for="address">地址</label>
		    <input type="text" class="form-control input-lg" name="address"  id="address" placeholder="请输入详细地址" value="${model.address }">
		  </div>
		   <div class="form-group">
		    <label for="telephone">电话</label>
		    <input type="text" class="form-control input-lg" name="telephone"  id="telephone" placeholder="请输入您的电话" value="${model.telephone }">
		  </div>
		  <button type="submit" class="btn btn-default btn-lg">保存</button>
		</form>
	</div>
</div>