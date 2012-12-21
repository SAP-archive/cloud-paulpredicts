package com.sap.pto.adapters;

import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.pto.util.configuration.ConfigUtil;

/**
 * This class uses the Mail Service, provided by the NetWeaver Cloud Platform,
 * which allows to send electronic mail messages from your Web applications
 * using e-mail providers that are accessible on the Internet. 
 */
public class MailAdapter {
    private static Logger logger = LoggerFactory.getLogger(MailAdapter.class);

    public static void send(String recipient, String subject, String mailContent) {
        String from = ConfigUtil.getProperty("mail", "mail.from");

        logger.debug("Sending mail from [" + from + "] with subject [" + subject + "] to [" + recipient + "]");

        try {
            Session session = getMailSession();
            session.getProperties().put("mail.from", from);

            Transport transport = session.getTransport();
            transport.connect();

            Message msg = createMessage(from, recipient, subject, mailContent, session);

            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();
        } catch (SendFailedException e) {
            logger.error("Failed to send email.", e);
        } catch (AddressException e) {
            logger.error("Invalid email address.", e);
        } catch (MessagingException e) {
            logger.error("Email message issue.", e);
        } catch (Exception e) {
            logger.error("Email error.", e);
        }
    }

    private static Session getMailSession() throws NamingException {
        InitialContext ctx = new InitialContext();
        Session session = (Session) ctx.lookup("java:comp/env/mail/Session");

        return session;
    }

    private static Message createMessage(String from, String recipient, String subject, String mailContent, Session session)
            throws AddressException, MessagingException {
        InternetAddress addressFrom = new InternetAddress(from);
        InternetAddress addressTo = new InternetAddress(recipient);
        Message message = new MimeMessage(session);
        message.setFrom(addressFrom);
        message.setRecipient(Message.RecipientType.TO, addressTo);
        message.setSubject(subject);
        message.setContent(mailContent, "text/plain");

        return message;
    }

    public static String getTemplate(String templateName) {
        try {
            return FileUtils.readFileToString(FileUtils.toFile(MailAdapter.class.getResource("/templates/" + templateName)));
        } catch (IOException e) {
            logger.error("Could not read template.", e);
        }

        return null;
    }

}
