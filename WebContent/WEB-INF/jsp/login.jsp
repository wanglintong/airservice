<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="../Wopop_files/style_log.css" rel="stylesheet"
	type="text/css">
<link rel="stylesheet" type="text/css"
	href="../Wopop_files/style.css">
<link rel="stylesheet" type="text/css"
	href="../Wopop_files/userpanel.css">
<link rel="stylesheet" type="text/css"
	href="../Wopop_files/jquery.ui.all.css">
<title>登录</title>
</head>
<body class="login">
	<div class="login_m">
		<div class="login_logo">
			<img src="../Wopop_files/logo.png" width="196" height="46">
		</div>
		<div class="login_boder">

			<div class="login_padding" id="login_model">
				<form action="${pageContext.request.contextPath }/login/login.do" method="post">
				<h2><font style="color:red">${msg}</font></h2>
				<h2>用户名</h2>
				<label> <input type="text" name="username" id="username"  autocomplete="off"
					class="txt_input txt_input2">
				</label>
				<h2>密码</h2>
				<label> <input type="password" name="password"
					id="userpwd" class="txt_input">
				</label>

				<div class="rem_sub">
					<div class="rem_sub_l">
						<input type="checkbox" name="rememberUser" id="save_me"> <label
							for="checkbox">记住用户</label>
					</div>
					<label> <input type="submit" class="sub_button"
						name="button" id="button" value="登录" style="opacity: 0.7;">
					</label>
				</div>
				</form>
			</div>
		</div>
		<!--login_boder end-->
	</div>
	<!--login_m end-->
	<br>
	<br>
</body>
</html>