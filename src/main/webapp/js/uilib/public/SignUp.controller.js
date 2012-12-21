sap.ui.controller("sap.pto.public.SignUp", {
    onInit : function() {
    },

    register : function(data) {
        jQuery.ajax({
            url : "/server/b/api/anonuserservice/users",
            type : 'POST',
            contentType : 'application/json',
            data : data,
            success : function(data) {
                sap.ui.commons.MessageBox.alert("Account was created successfully. You will be redirected to the home page now.",
                        "", "Success");
                window.location = '/server';
            },
            error : function(data) {
                sap.ui.commons.MessageBox.alert("Could not create new user: " + data.responseText, "", "Error");
            }
        });
    }
});
