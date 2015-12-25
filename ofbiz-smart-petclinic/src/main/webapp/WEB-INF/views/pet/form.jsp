<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="row"  style="margin-top: 50px;">
	<div class="col-lg-12" >
		<ol class="breadcrumb">
		  <li><a href="${ctxPath }/">首页</a></li>
		  <li><a href="${ctxPath }/list${uriSuffix}">宠物</a></li>
		  <li class="active">
		  	<c:choose>
		  		<c:when test="${not empty(model) }">${model.name }</c:when>
		  		<c:otherwise>新增宠物</c:otherwise>
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
		  <div class="form-group">
		    <label for="firstName">主人</label>
		    <p>${owner.firstName }${owner.lastName }</p>
		  </div>
		  <div class="form-group">
		    <label for="name">名字</label>
		    <input type="text" class="form-control input-lg" name="name"  id="name" placeholder="请输入您名字">
		  </div>
		   <div class="form-group">
		    <label for="birthday">生日</label>
		    <input type="text" class="form-control input-lg" name="birthday"  id="birthday" placeholder="请输入您所在的城市">
		  </div>
		  <div class="form-group">
		    <label for="birthday">类型</label>
		    <select class="form-control input-lg" name="type.id">
		    	<c:forEach items="${petTypes }" var="pt">
		    		<option value="${pt.id }">${pt.name }</option>
		    	</c:forEach>
		    </select>
		  </div>
		  <button type="submit" class="btn btn-default btn-lg">保存</button>
		</form>
	</div>
</div>