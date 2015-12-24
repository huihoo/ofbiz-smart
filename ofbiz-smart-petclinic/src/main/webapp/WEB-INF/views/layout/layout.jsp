<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>${pageTitle }</title>
<link rel="stylesheet" href="/css/bootstrap.min.css">
${moreCss }
</head>
<body>

<h1>FIND OWNERES</h1> ${name }

<div>
	<jsp:include page="${layoutContentView}"/>
</div>

<script src="http://cdn.bootcss.com/jquery/1.11.3/jquery.min.js"></script>
<script src="/js/bootstrap.min.js"></script>


${moreJavascripts }
</body>
</html>