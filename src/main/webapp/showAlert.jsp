<%@ include file="header.jsp"%>

<script type="text/javascript">
    var message = "";

    message = <%="\""
					+ request.getSession().getAttribute("message").toString()
					+ "\""%>;
    alert(message);
</script>
</head>
</html>