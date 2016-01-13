<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
	request.setAttribute("now", new java.util.Date());
%>
<div class="row"  style="margin-top: 50px;">
	<div class="col-lg-12" >
		<ol class="breadcrumb">
		  <li><a href="${ctxPath }/">首页</a></li>
		  <li><a href="${ctxPath }/pet/view${uriSuffix }?id=${petId}">宠物</a></li>
		  <li class="active">
		  	新增访问
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
	  			<c:set var="action" value="${ctxPath }/visit/update${uriSuffix}"></c:set>
	  		</c:when>
	  		<c:otherwise>
	  			<c:set var="action" value="${ctxPath }/visit/save${uriSuffix}"></c:set>
	  		</c:otherwise>
	  	</c:choose>
		 
		 <table class="table table-hover table-bordered">
		  <caption>宠物信息</caption>
		  <thead>
		  	<tr>
		  		<th>名字</th>
		  		<th>生日</th>
		  		<th>类型</th>
		  		<th>主人</th>
		  	</tr>
		  </thead>
	      <tbody>
	      	<tr>
	      		<td>${pet.name}</td>
	      		<td><fmt:formatDate value="${pet.birthdayDate}" pattern="yyyy-MM-dd"/></td>
	      		<td>${pet.type.name}</td>
	      		<td>${pet.owner.firstName}${pet.owner.lastName}</td>
	      	</tr>
	      	
	      </tbody>
	    </table>
	     	
		<form action="${action}" method="post">
		  <input type="hidden" value="${model.id }" name="id"/>
		  <c:if test="${empty(model) }">
		  	<input type="hidden" value="${petId }" name="pet.id"/>
		  </c:if>
		  <div class="form-group">
		    <label for="birthday">访问日期</label>
		    <input type="text" class="form-control input-lg" name="date"  id="date" 
		          value="<fmt:formatDate value="${now}" pattern="yyyy-MM-dd"/>" readonly="readonly">
		  </div>
		  
		  <div class="form-group">
		    <label for="firstName">描述</label>
		    <input type="text" class="form-control input-lg" name="description"  id="description">
		  </div>
		  		   
		  <button type="submit" class="btn btn-default btn-lg">保存</button>
		</form>
	</div>
</div>