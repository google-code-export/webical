<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xml:lang="en" lang="en" xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Webical Nightly-build login</title>
	<meta http-equiv="Content-type" content="text/html; charset=UTF-8" />
	<link href="../css/style.css" rel="stylesheet" type="text/css" /> 
</head>

<body>
	<div id="login">
		<form method="post" action="j_security_check">
		<h1>Webical Login</h1>
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
		This demo is a nightly build and reflects the latest changes in the application. This means it could very well be broken :). 
		To see the latest release go to <a href="http://demo.webical.org">The main demo site</a> Use webical/webical as the username/
		password to login here.
		</p>
	</div>
</body>
</html>