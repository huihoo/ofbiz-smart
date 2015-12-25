<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div class="row" style="margin-top: 100px; ">
	<div class="col-lg-12 table-responsive" >
		<table class="table table-hover table-bordered">
	      <thead>
	        <tr>
	          <th>#</th>
	          <th>姓氏</th>
	          <th>名字</th>
	          <th>操作</th>
	        </tr>
	      </thead>
	      <tbody>
	      	<c:forEach items="${list }" var="c">
	      	 	<tr>
		          <th scope="row">${c.id }</th>
		          <td>${c.firstName }</td>
		          <td>${c.lastName }</td>
		          <td>@mdo</td>
		        </tr>	
	      	</c:forEach>
	       
	      </tbody>
	    </table>
	</div>
</div>