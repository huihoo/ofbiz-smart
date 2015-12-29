<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="hh" uri="http://huihoo.com/tags" %>

<div class="row"  style="margin-top: 50px;">
	<div class="col-lg-12" >
		<ol class="breadcrumb">
		  <li><a href="${ctxPath }/">首页</a></li>
		  <li><a href="${ctxPath }/pet/list${uriSuffix}">宠物</a></li>
		  <li class="active">
		  	<c:choose>
		  		<c:when test="${not empty(model) }">${model.name }</c:when>
		  		<c:otherwise>新增宠物</c:otherwise>
		  	</c:choose>
		  </li>
		</ol>
		
		<c:if test="${not empty(error)}">
			<div class="alert alert-danger alert-dismissible fade in" role="alert">
		      <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">×</span></button>
		      <h4>:(  抱歉，操作失败!</h4>
		      <p>原因：${errorMessage }</p>
		      <c:if test="${not empty(validationErrors) }">
		      	<c:forEach items="${validationErrors}" var="ve">
		      		<ul>
		      			<li>${ve.key }
		      				<ul>
		      					<c:forEach items="${ve.value }" var="vee">
		      						<li>${vee.filedMessage }</li>
		      					</c:forEach>
		      				</ul>
		      			</li>
		      		</ul>
		      	</c:forEach>
		      </c:if>
		    </div>
		</c:if>
		
		<c:choose>
	  		<c:when test="${not empty(model) }">
	  			<c:set var="action" value="${ctxPath }/pet/update${uriSuffix}"></c:set>
	  		</c:when>
	  		<c:otherwise>
	  			<c:set var="action" value="${ctxPath }/pet/save${uriSuffix}"></c:set>
	  		</c:otherwise>
	  	</c:choose>
		  	
		<form action="${action}" method="post">
		  <input type="hidden" value="${model.id }" name="id"/>
		  <c:if test="${empty(model) }">
		  	<input type="hidden" value="${ownerId }" name="owner.id"/>
		  </c:if>
		  <div class="form-group">
		    <label for="firstName">主人</label>
		    <c:choose>
		    	<c:when test="${not empty(model) }">
		    		 <p>${model.owner.firstName }${model.owner.lastName }</p>
		    	</c:when>
		    	<c:otherwise>
		    		 <p>${owner.firstName }${owner.lastName }</p>
		    	</c:otherwise>
		    </c:choose>
		   
		  </div>
		  <div class="form-group">
		    <label for="name">名字</label>
		    <input type="text" class="form-control input-lg" name="name"  id="name" placeholder="请输入名字" value="${model.name }">
		  </div>
		   <div class="form-group">
		    <label for="birthday">生日</label>
		    <input type="text" class="form-control input-lg" name="birthdayDate"  
		                      id="birthdayDate" 
		                      value="<fmt:formatDate value="${model.birthdayDate }" pattern="yyyy-MM-dd"/>">
		  </div>
		  <div class="form-group">
		    <label for="birthday">类型</label>
		    <select class="form-control input-lg" name="type.id">
		    	<hh:options className="org.huihoo.samples.petclinic.model.PetType" 
		    				currentId="${model.type.id }"
		    	            liveTimeInSeconds="300"/>
		    </select>
		  </div>
		  <button type="submit" class="btn btn-default btn-lg">保存</button>
		</form>
	</div>
</div>