sap.ui.jsview("sap.pto.public.SignUp", {

    getControllerName : function() {
        return "sap.pto.public.SignUp";
    },

    createContent : function(controller) {
        var _this = this;
        var vLayout = new sap.ui.commons.layout.VerticalLayout();
        var paulImage = new sap.ui.commons.Image({
            src : "public/img/paul.png",
            width : "120px"
        });
        paulImage.addStyleClass("bottommarginbig");

        if (!SHIROMODE) {
            var intro = new sap.ui.commons.TextView({
                text : "Manual user registration is not possible with the configured ID Service.",
                design : sap.ui.commons.TextViewDesign.H1
            });
            intro.addStyleClass("leftpaddingbig");
            vLayout.addContent(stashHorizontally(paulImage, intro));
            return vLayout;
        }

        var intro = new sap.ui.commons.TextView({
            text : "Fill out the fields below to register.",
            design : sap.ui.commons.TextViewDesign.H1
        });
        intro.addStyleClass("leftpaddingbig");
        vLayout.addContent(stashHorizontally(paulImage, intro));

        var form = new sap.ui.commons.layout.MatrixLayout({
            layoutFixed : false,
            columns : 2,
            widths : [ "120px", "50%" ]
        });

        var username = new sap.ui.commons.TextField();
        addToFormMatrix(form, "Username", username);

        var email = new sap.ui.commons.TextField();
        addToFormMatrix(form, "E-Mail", email);

        var password = new sap.ui.commons.PasswordField();
        addToFormMatrix(form, "Password", password);

        var submit = new sap.ui.commons.Button({
            text : "Submit",
            press : function() {
                var data = '{"userName":"' + username.getValue() + '", "email":"' + email.getValue() + '", "password":"'
                        + password.getValue() + '"}';
                _this.getController().register(data);
            }
        });
        form.createRow(null, matrixRight(submit));

        vLayout.addContent(form);

        return vLayout;
    }

});
