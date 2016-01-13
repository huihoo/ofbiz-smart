<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="row" style="margin-top: 50px; ">
	
		
	<div class="col-lg-12 table-responsive" >
		<ol class="breadcrumb">
		  <li><a href="${ctxPath }/">首页</a></li>
		  <li class="active">宠物医生</li>
		</ol>
		
		
		
		<table class="table table-hover table-bordered">
	      <thead>
	        <tr>
	          <th>#</th>
	          <th>名字</th>
	          <th>专业</th>
	        </tr>
	      </thead>
	      <tbody>
	      	<c:forEach items="${list }" var="c">
	      	 	<tr>
		          <th scope="row">${c.id }</th>
		          <td>${c.firstName }${c.lastName }</td>
		          <td>
		          	<c:choose>
		          		<c:when test="${not empty(c.specialties) }">
		          			<c:forEach items="${c.specialties}" var="cs">
				          		${cs.name }&nbsp;
				          	</c:forEach>
		          		</c:when>
		          		<c:otherwise>none</c:otherwise>
		          	</c:choose>
		          	
		          </td>
		        </tr>	
	      	</c:forEach>
	       
	      </tbody>
	    </table>
	</div>
</div>