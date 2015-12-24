<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>${pageTitle }</title>
<link rel="stylesheet" href="${ctxPath }/css/bootstrap.min.css">
<link rel="stylesheet" href="${ctxPath }/css/nav.css">
${moreCss }

<!-- HTML5 shim and Respond.js for IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
  <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
  <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
<![endif]-->
</head>
<body>

<div class="container">
     
      <div class="masthead">
        <h3 class="text-muted">OFBiz Smart演示应用(在线宠物医院)</h3>
        <nav>
          <ul class="nav nav-justified">
            <li class="active"><a href="#">首页</a></li>
            <li><a href="#">宠物主人</a></li>
            <li><a href="#">宠物</a></li>
            <li><a href="#">宠物医生</a></li>
            <li><a href="#">关于我们</a></li>
            <li><a href="#">联系我们</a></li>
          </ul>
        </nav>
      </div>

      <jsp:include page="${layoutContentView}"/>

      <!-- Site footer -->
      <footer class="footer">
        <p>&copy; Powered by OFBiz Smart.</p>
      </footer>

    </div> 



<script src="http://cdn.bootcss.com/jquery/1.11.3/jquery.min.js"></script>
<script src="${ctxPath }/js/bootstrap.min.js"></script>


${moreJavascripts }
</body>
</html>