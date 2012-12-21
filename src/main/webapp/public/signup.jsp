<%@ include file="../header.jsp"%>
<title>Paul Predicts - Register New User</title>

<script type="text/javascript">
    <jsp:include page="js/common_utils.js" />
    <jsp:include page="js/ui_utils.js" />
    <jsp:include page="js/date.js" />
    <jsp:include page="js/user_utils.js" />

    shell = new sap.ui.ux3.Shell("userShell", {
        appTitle : "Paul Predicts - Register New user",
        showFeederTool : false,
        showInspectorTool : false,
        showSearchTool : false,
        showLogoutButton : false
    });
    loadSystemInfo();

    shell.setContent(getView("sap.pto.public.SignUp"));
    shell.placeAt("shellArea");
</script>

</head>

<body class="sapUiBody" role="application">
	<div id="shellArea"></div>
</body>
</html>
