package com.sap.pto.adapters;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisNameConstraintViolationException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.ecm.api.EcmService;
import com.sap.ecm.api.RepositoryOptions;

/**
 * Class that accesses functionalities of ECM. ECM is Document Service provided by
 * the NetWeaver Cloud Platform. Used for storing and retrieving document
 * content in a folder like structure. In the current application ECM is used
 * for storing images.
 */
public class DocumentAdapter {
    private static Logger logger = LoggerFactory.getLogger(DocumentAdapter.class);
    private static Session cmisSession = null;
    private static final String UNIQUE_NAME = "com.sap.pto.ecm.document.repository.01";
    private static final String UNIQUE_KEY = "com.sap.pto.ecm.h14PJthdmpskFFjlk";

    public static void uploadDocument(String documentName, byte[] documentContent) throws CmisNameConstraintViolationException {
        if (documentExists(documentName)) {
            deleteDocument(documentName);
        }
        createDocument(documentName, documentContent);
    }

    private static boolean documentExists(String documentName) {
        try {
            getDocument(documentName);
        } catch (CmisObjectNotFoundException e) {
            return false;
        }

        return true;
    }

    private static Document getDocument(String documentName) {
        Document document = null;
        Session session = getCmisSession();
        if (session == null) {
            logger.error("ECM not found, Session is null.");
            return null;
        }
        try {
            document = (Document) session.getObjectByPath("/" + documentName);
        } catch (ClassCastException e) {
            logger.error("The path does not point to a Document.", e);
        }

        return document;
    }

    private static void createDocument(String documentName, byte[] documentContent) throws CmisNameConstraintViolationException {
        Folder root = getCmisSession().getRootFolder();

        Map<String, Object> properties = getProperties(documentName);
        String documentExtension = documentName.substring(documentName.lastIndexOf('.') + 1);
        String mimeType = "image/" + documentExtension;
        ContentStream contentStream = getContentStream(documentName, mimeType, documentContent);
        root.createDocument(properties, contentStream, VersioningState.NONE);
    }

    private static Map<String, Object> getProperties(String documentName) {
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
        properties.put(PropertyIds.NAME, documentName);
        return properties;
    }

    private static ContentStream getContentStream(String documentName, String mimeType, byte[] documentContent) {
        InputStream stream = new ByteArrayInputStream(documentContent);
        ContentStream contentStream = getCmisSession().getObjectFactory().createContentStream(documentName, documentContent.length,
                mimeType, stream);
        return contentStream;
    }

    public static byte[] getDocumentAsByteArray(String documentName) throws CmisObjectNotFoundException {
        Document document = getDocument(documentName);
        byte[] documentAsByteArray = convertDocumentToByteArray(document);
        return documentAsByteArray;
    }

    private static byte[] convertDocumentToByteArray(Document document) {
        InputStream stream = document.getContentStream().getStream();
        byte[] documentAsBytes = null;
        try {
            documentAsBytes = IOUtils.toByteArray(stream);
            IOUtils.closeQuietly(stream);
        } catch (IOException e) {
            logger.error("Could not convert document to byte array.", e);
        }

        return documentAsBytes;
    }

    public static void deleteDocument(String documentName) {
        try {
            Document documentToBeDeleted = getDocument(documentName);
            documentToBeDeleted.deleteAllVersions();
        } catch (CmisObjectNotFoundException e) {
            logger.info("Document '" + documentName + "' does not exist in repository. Cannot be deleted.", e);
        }
    }

    private static Session getCmisSession() {
        if (cmisSession == null) {
            Session session = null;
            try {
                InitialContext ctx = new InitialContext();
                String lookupName = "java:comp/env/EcmService";
                EcmService ecmSvc = (EcmService) ctx.lookup(lookupName);
                try {
                    // connect to my repository
                    session = ecmSvc.connect(UNIQUE_NAME, UNIQUE_KEY);
                    logger.info("Connection to ECM repository established.");
                } catch (CmisObjectNotFoundException e) {
                    // repository does not exist, so try to create it
                    RepositoryOptions options = new RepositoryOptions();
                    options.setUniqueName(UNIQUE_NAME);
                    options.setRepositoryKey(UNIQUE_KEY);
                    options.setVisibility(com.sap.ecm.api.RepositoryOptions.Visibility.PROTECTED);
                    options.setMultiTenantCapable(true);
                    ecmSvc.createRepository(options);
                    // should be created now, so connect to it
                    session = ecmSvc.connect(UNIQUE_NAME, UNIQUE_KEY);
                    logger.info("Connection to new ECM repository established.");
                }
            } catch (NamingException e) {
                logger.error("Could not find the ECM service.", e);
            }
            cmisSession = session;
        }
        return cmisSession;
    }
}
