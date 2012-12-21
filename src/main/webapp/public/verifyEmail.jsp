<%@ include file="../header.jsp"%>

<title>Paul Predicts - E-Mail Verification</title>

<script>
function sendEmailConfirmationRequest(confirmationKey) {
    jQuery.ajax({  	 
        url : "/server/b/api/anonuserservice/verifymail/" + confirmationKey ,
        type : 'POST',
        success : function() {
        	window.location.href = "mailVerificationSuccess.jsp";
        },
        error : function() {
        	// TODO: for some reason the service returns an error although it succeeded
            window.location.href = "mailVerificationSuccess.jsp";
        	// alert("This E-Mail is already verified.");
        }
    });	
}
</script>
</head>
<body class="sapUiBody" role="application">
	<div id="shellArea"></div>
	<script>
		sendEmailConfirmationRequest('<%=request.getParameter("emailConfirmationKey")%>');
    </script>
</body>
</html>
