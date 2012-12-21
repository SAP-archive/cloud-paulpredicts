<%@ include file="header.jsp"%>
<title>Paul Predicts</title>

<script type="text/javascript">
    <jsp:include page="js/common_utils.js" />
    <jsp:include page="js/ui_utils.js" />
    <jsp:include page="js/date.js" />
    <jsp:include page="js/user_utils.js" />
    <jsp:include page="js/uilib/user/shell.js" />

    initPaul();
    setInitialShellContent("home");
</script>

</head>

<body class="sapUiBody" role="application">
	<div id="shellArea"></div>
</body>
</html>
