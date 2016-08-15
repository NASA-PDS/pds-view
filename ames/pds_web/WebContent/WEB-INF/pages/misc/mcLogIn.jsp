<%@ taglib prefix="s" uri="/struts-tags" %>
<div class="defaultWrapper">
	<div class="basicBox" style="font-size: 12px;">
		<form action="<s:url action="MCAuthenticate" />" method="post" >
			<div class="label">Username</div>
			<div class="contents"><input type="text" name="username" /></div>
			
			<div class="label">Password</div>
			<div class="contents"><input type="password" name="password" /></div>
			
			<div class="contents"><input type="submit" name="submit" value="Log in" /></div>
		</form>
	</div>
</div>