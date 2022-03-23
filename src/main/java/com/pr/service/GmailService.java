package com.pr.service;

import com.pr.model.GmailMessage;
import com.pr.model.Gmail;
import org.apache.commons.mail.util.MimeMessageParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class GmailService {
    private Gmail gmail;

    public void setGmail(Gmail gmail) {
        this.gmail = gmail;
    }

    @Autowired
    private JavaMailSender mailSender;

    public void sendM() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(gmail.getTo());
        message.setSubject(gmail.getSubject());
        message.setText(gmail.getMessage());
        mailSender.send(message);
    }

    public void sendMwithAttachment() throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
        messageHelper.setTo(gmail.getTo());
        messageHelper.setSubject(gmail.getSubject());
        messageHelper.setText(gmail.getMessage());
        messageHelper.addAttachment(gmail.getAttachment(), new ClassPathResource(gmail.getAttachment()));
        mailSender.send(message);
    }

    public List<GmailMessage> getListM() throws MessagingException {
        Folder folder = null;
        Store store = null;
        List<GmailMessage> messageList = new ArrayList<>();

        try {
            Properties props = System.getProperties();
            props.setProperty("mail.store.protocol", "imaps");

            Session session = Session.getDefaultInstance(props, null);
            store = session.getStore("imaps");
            store.connect("imap.gmail.com","", "");
            folder = store.getFolder("Inbox");

            folder.open(Folder.READ_WRITE);
            Message messages[] = folder.getMessages();
            System.out.println("Number of messages: " + folder.getMessageCount());
            System.out.println("Number of unread messages in inbox: " + folder.getUnreadMessageCount());
            for (int i=0; i < messages.length; ++i) {
                Message message = messages[i];
                messageList.add(new GmailMessage(i,message.getSubject(),""+message.getFrom()[0], readContent((MimeMessage) message)));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (folder != null) {
                folder.close(true);
            }
            if (store != null) {
                store.close();
            }
        }
        return messageList;
    }

    public String readContent(MimeMessage message) throws Exception {
        return new MimeMessageParser(message).parse().getPlainContent();
    }
}
