package com.sap.pto.services;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.chemistry.opencmis.commons.exceptions.CmisNameConstraintViolationException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;

import com.sap.pto.adapters.DocumentAdapter;
import com.sap.pto.dao.UserDAO;
import com.sap.pto.dao.entities.User;
import com.sap.pto.util.Consts;
import com.sap.pto.util.UploadHandler;
import com.sap.pto.util.UserUtil;

@Path("userservice")
public class UserService extends BasicService {
    @GET
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON)
    public User getCurrentUser() {
        return userUtil.getLoggedInUser(request);
    }

    @POST
    @Path("/logout")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logoutCurrentUser() {
        UserUtil.logOut(request);

        return RESPONSE_OK;
    }

    @POST
    @Path("/uploadimage")
    public Response uploadPicture() {
        UserUtil userUtil = UserUtil.getInstance();
        User user = userUtil.getLoggedInUser(request);

        try {
            FileItem document = UploadHandler.getUploadedDocument(request);
            byte[] documentContent = document.get();
            if (!UploadHandler.isImage(documentContent)) {
                alert("The file that you tried to upload is not a supported image. " + "Please select another one.");
                return RESPONSE_BAD;
            }
            if (documentContent.length > UploadHandler.DEFAULT_MAX_SIZE) {
                alert("The file that you tried to upload is too big. " + "Please resize or select another one.");
                return RESPONSE_BAD;
            }
            if (!UploadHandler.isImageSizeValid(documentContent, Consts.MAX_PROFILE_IMAGE_WIDTH, Consts.MAX_PROFILE_IMAGE_HEIGHT)) {
                alert("The image that you have choosen exceeds " + Consts.MAX_PROFILE_IMAGE_WIDTH + "x" + Consts.MAX_PROFILE_IMAGE_HEIGHT
                        + " pixels. Please select a smaller one.");
                return RESPONSE_BAD;
            }
            String path = "/server/b/api/userservice/user/image/";
            String oldImageLink = user.getImageLink();
            if (oldImageLink != null && oldImageLink.contains(path)) {
                // in case that the old picture is stored in the ECM repository, delete it
                String oldPictureName = oldImageLink.substring(oldImageLink.indexOf(path) + path.length());
                DocumentAdapter.deleteDocument(oldPictureName);
            }

            String fileExtension = document.getName().substring(document.getName().lastIndexOf('.') + 1);
            String imageName = user.getUserName() + "." + fileExtension;
            DocumentAdapter.uploadDocument(imageName, document.get());

            user.setImageLink(path + user.getUserName() + "." + fileExtension);
            user = UserDAO.save(user);

            document.delete();
        } catch (FileUploadException e) {
            alert("File could not be uploaded.");
            return RESPONSE_BAD;
        } catch (CmisNameConstraintViolationException exc) {
            alert("The image could not be uploaded. This functionality is not available currently. Please try again later.");
            return RESPONSE_BAD;
        }

        return RESPONSE_OK;
    }

    @GET
    @Path("/user/image/{filename}")
    @Produces({ "image/jpeg,image/png,image/gif,image/bmp" })
    public Response pictureVisualizer(@PathParam("filename") String filename) {
        try {
            byte[] pictureAsBytes = DocumentAdapter.getDocumentAsByteArray(filename);
            if (pictureAsBytes != null) {
                String fileExtension = filename.substring(filename.lastIndexOf('.') + 1);
                String mimeType = "image/" + fileExtension;

                return Response.ok(pictureAsBytes, mimeType).build();
            }
        } catch (CmisObjectNotFoundException e) {
            // this user's picture was not found in the ECM repository, thus use
            // the default one
            String userName = filename.substring(0, filename.lastIndexOf('.'));
            User user = UserDAO.getUserByUserName(userName);
            user.setImageLink(User.DEFAULT_IMAGELINK);
            UserDAO.save(user);
        }

        return RESPONSE_BAD;
    }
}
