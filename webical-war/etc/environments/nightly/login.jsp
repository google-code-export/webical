<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="en" lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>webical nightly-build login</title>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
	<link href="http://demo.webical.org/css/style.css" rel="stylesheet" type="text/css" /> 
</head>

<body>
	<div id="login">
		<form method="post" action="j_security_check">
		<h1>Login</h1>
		<div class="formItem">
			<label for="username">Username: </label>
			<span class="formElement"><input type="text" name="j_username" id="username" value="webical" /></span>
		</div>
		<div class="formItem">
			<label for="password">Password: </label>
			<span class="formElement"><input id="password" type="password" name="j_password" value="webical" /></span>
		</div>
		<div class="formItem">
			<span class="formElement"><input type="submit" value="login" class="button"/></span>
		</div>
		</form>
		<p>
		This is a public demo site used to display webical's progress. This is a nightly build and it could very well be broken:). 
		To see the last release go to <a href="http://demo.webical.org">The main demo site</a> Use webical/webical as the username/
		password to login here.
		</p>
	</div>
</body>
</html>