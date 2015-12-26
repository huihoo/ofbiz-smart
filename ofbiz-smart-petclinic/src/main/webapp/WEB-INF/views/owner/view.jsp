<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<div class="row" style="margin-top: 50px; ">
	
	<div class="col-lg-12" >
		<ol class="breadcrumb">
		  <li><a href="${ctxPath }/">首页</a></li>
		  <li><a href="${ctxPath }/owner/list${uriSuffix}">宠物主人</a></li>
		  <li class="active">${model.firstName }</li>
		</ol>
	
		<h4>宠物主人详细信息</h4>
		
		<table class="table table-hover table-striped">
		  
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
	    
	    <h4>宠物和访问记录</h4>
		
	    <c:forEach items="${model.pets }" var="p">
	    	<table class="table table-striped">
	    		<tr>
	    			<td width="200px;">
	    				<dl>
				      		<dt  style="float: left;clear: left;text-align: right;text-overflow: ellipsis;"><strong>名字</strong></dt>
				      		<dd >&nbsp;&nbsp;${p.name }</dd>
				      		
				      		<dt  style="float: left;clear: left;text-align: right;text-overflow: ellipsis;"><strong>生日</strong></dt>
				      		<dd>&nbsp;&nbsp;<fmt:formatDate value="${p.birthdayDate }" pattern="yyyy-MM-dd"/></dd>
				      		
				      		<dt  style="float: left;clear: left;text-align: right;text-overflow: ellipsis;"><strong>类型</strong></dt>
				      		<dd>&nbsp;&nbsp;${p.type.name }</dd>
				      		
				      	</dl>
	    			</td>
	    			<td>
	    				<table class="table table-bordered">
	    					<tbody>
	    						<tr>
	    							<th>访问日期</th>
	    							<th>描述</th>
	    						</tr>
	    					</tbody>
	    					<tbody>
	    						<c:forEach items="${p.visits }" var="pv">
	    							<tr>
	    								<td><fmt:formatDate value="${pv.date }" pattern="yyyy-MM-dd"/></td>
	    								<td>${pv.description }</td>
	    							</tr>
	    						</c:forEach>
	    					</tbody>
	    					<tfoot>
	    						<tr>
	    							<td>
	    								<a href='${ctxPath }/pet/edit${uriSuffix }?id=${p.id}' class="btn btn-success">
	    									<i class="icon-edit"></i>&nbsp;编辑宠物
	    								</a>
	    							</td>
	    							<td>
	    								<a href='${ctxPath }/visit/create${uriSuffix }?petId=${p.id}' class="btn btn-primary">
	    									<i class="icon-plus"></i>&nbsp;增加访问
	    								</a>
	    							</td>
	    						</tr>
	    					</tfoot>
	    				</table>
	    			</td>
	    		</tr>
	    	</table>
	    </c:forEach>
	</div>
</div>