<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
	
<%@page import = "java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Purdue Search</title>
</head>
<body>
	<br />
	<br />
	<form method="post" name="frm" action="Search">
		<table border="0" width="300" align="center" bgcolor="#e9f">
			<tr>
				<td colspan=2 style="font-size: 12pt;" align="center">
					<h3>Search User</h3>
				</td>
			</tr>
			<tr>
				<td><b>User Name</b></td>
				<td>: <input type="text" name="pid" id="pid">
				</td>
			</tr>
			<tr>
				<td colspan=2 align="center"><input type="submit" name="submit"
					value="Search"></td>
			</tr>
		</table>
	</form>
	
		<%
			

				ArrayList urls = new ArrayList<String>();
				ArrayList images = new ArrayList<String>();
				ArrayList descriptions = new ArrayList<String>();
				if (request.getAttribute("urls") != null) {
					urls = (ArrayList) request.getAttribute("urls");
				}
				if (request.getAttribute("images") != null) {
					images = (ArrayList) request.getAttribute("images");
				}
				if (request.getAttribute("descriptions") != null) {
					descriptions = (ArrayList)request.getAttribute("descriptions");
				}
			
		%>
		
		<% if (urls.size() > 0 && images.size() > 0) { %>
		<%for (int i = 0; i < urls.size(); i++) {%>
		<p><%=urls.get(i) %></p>
		<p><%=descriptions.get(i) %>
		<a href=<%=urls.get(i) %>>
		<img src=<%=images.get(i) %> height = 200 width = 200>
		</a>
		<%} %>
		<%} %>
		
		
		
		
</body>
</html>