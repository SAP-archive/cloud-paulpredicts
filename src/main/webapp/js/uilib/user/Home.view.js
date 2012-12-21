sap.ui.jsview("sap.pto.user.Home", {

    getControllerName : function() {
        return "sap.pto.user.Home";
    },

    createContent : function(controller) {
        var _this = this;
        var paulID = sysinfoDataModel.getProperty("/paulid");
        var userID = sap.ui.getCore().getModel().getProperty("/id");

        var paulName = new sap.ui.commons.TextView({
            text : "Paul",
            design : sap.ui.commons.TextViewDesign.H1
        });
        var userName = new sap.ui.commons.TextView({
            text : "{/fullName}",
            design : sap.ui.commons.TextViewDesign.H1
        });
        var paulImage = new sap.ui.commons.Image({
            src : "public/img/paul.png"
        });
        paulImage.addStyleClass("profilePicture");

        var userImage = new sap.ui.commons.Image({
            src : "{/imageLink}",
            tooltip : "Click to change your avatar.",
            press : function(oEvent) {
                _this.openDialog();
            }
        });
        userImage.addStyleClass("profilePicture");

        var paulSuccess = new sap.ui.commons.TextView({
            text : _this.getController().getUserSuccessRate(paulID) + "%",
            design : sap.ui.commons.TextViewDesign.H1
        });
        var userSuccess = new sap.ui.commons.TextView({
            text : _this.getController().getUserSuccessRate(userID) + "%",
            design : sap.ui.commons.TextViewDesign.H1
        });

        var panel = createPanel("Competition Statistics");
        var homeLayout = new sap.ui.commons.layout.MatrixLayout();
        homeLayout.addStyleClass("matrixCenter");

        homeLayout.createRow(paulName, userName);
        homeLayout.createRow(paulImage, userImage);
        homeLayout.createRow(paulSuccess, userSuccess);
        panel.addContent(homeLayout);

        return boxify(panel);
    },

    openDialog : function() {
        var userImageInDialog = new sap.ui.commons.Image({
            src : "{/imageLink}"
        });
        userImageInDialog.addStyleClass("profilePicture");
        var oText = new sap.ui.commons.TextView({
            text : "Your Photo",
            design : sap.ui.commons.TextViewDesign.H2
        });
        var imageUploader = new sap.ui.commons.FileUploader({
            uploadUrl : "/server/b/api/userservice/uploadimage",
            name : "uploader",
            uploadOnChange : true,
            uploadComplete : function(oEvent) {
                loadRestDataSync(sap.ui.getCore().getModel(), "/server/b/api/userservice/user");
                // avoid browser caching
                var newImageSrcValue = sap.ui.getCore().getModel().getProperty("/imageLink") + "?refresh=" + Math.random();
                sap.ui.getCore().getModel().setProperty("/imageLink", newImageSrcValue);
            }
        });
        var changeImageDialog = new sap.ui.commons.Dialog({
            contentBorderDesign : sap.ui.commons.enums.BorderDesign.Box,
            modal : true
        });
        var changeImageDialogLayout = new sap.ui.commons.layout.MatrixLayout({
            width : "500px",
            height : "300px"
        });
        changeImageDialogLayout.addStyleClass("matrixCenter");

        changeImageDialogLayout.createRow(oText, userImageInDialog);
        changeImageDialogLayout.createRow(imageUploader);

        changeImageDialog.setTitle("Personalize");
        changeImageDialog.addContent(changeImageDialogLayout);
        changeImageDialog.addButton(new sap.ui.commons.Button({
            text : "OK",
            width : "100px",
            height : "25px",
            press : function() {
                changeImageDialog.close();
            }
        }));
        changeImageDialog.open();
    }

});
