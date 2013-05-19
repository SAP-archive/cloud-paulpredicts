package com.sap.pto.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.swing.ImageIcon;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * The class is used for validating and managing file uploads. 
 */
public class UploadHandler {
    public final static int DEFAULT_MAX_SIZE = 100 * 1000 * 1024;
    //image related
    private static final String HEADER_OF_BMP_FILE = "424d";
    private static final String HEADER_OF_JPEG_FILE = "ffffffffffffffd8ffffffffffffffe0";
    private static final String HEADER_OF_GIF_FILE = "47494638";
    private static final String HEADER_OF_PNG_FILE = "ffffff89504e47";
    private static HashSet<String> ALLOWED_IMAGE_HEADERS = new HashSet<String>(Arrays.asList(HEADER_OF_JPEG_FILE, HEADER_OF_GIF_FILE,
            HEADER_OF_PNG_FILE));

    public static FileItem getUploadedDocument(HttpServletRequest request) throws FileUploadException {
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);
        List<FileItem> items = upload.parseRequest(request);
        Iterator<FileItem> iterator = items.iterator();
        FileItem document = (FileItem) iterator.next();

        return document;
    }

    public static boolean isImage(byte[] documentContent) {
        boolean result = false;
        int neededHeaderBytes = 4;
        StringBuilder header = new StringBuilder();
        for (int i = 0; i < neededHeaderBytes; i++) {
            header.append(Integer.toHexString(documentContent[i]));
        }

        if (ALLOWED_IMAGE_HEADERS.contains(header.toString()) || header.toString().startsWith(HEADER_OF_BMP_FILE)) {
            result = true;
        }

        return result;
    }

    public static boolean isImageSizeValid(byte[] documentContent, int maxWidth, int maxHeight) {
        boolean result = false;
        ImageIcon icon = new ImageIcon(documentContent);
        int width = icon.getIconWidth();
        int height = icon.getIconHeight();

        if (width <= maxWidth && height <= maxHeight) {
            result = true;
        }

        return result;
    }
}