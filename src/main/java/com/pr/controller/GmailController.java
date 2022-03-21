package com.pr.controller;

import com.pr.model.GmailMessage;
import com.pr.model.Gmail;
import com.pr.service.GmailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

@Controller()
public class GmailController {
    private final GmailService gmailService;

    public GmailController(GmailService gmailService) {
        this.gmailService = gmailService;
    }

    @GetMapping
    public String newM(Model gmail) {
        gmail.addAttribute("mail", new Gmail());
        return "send";
    }

    @GetMapping("inbox")
    public String showInbox(Model gmail) throws Exception {

        List<GmailMessage> messageList = gmailService.getListM();
        gmail.addAttribute("messageList", messageList);
        return "inbox";
    }

    @GetMapping("inbox/message/{id}")
    public String showM(@PathVariable(value = "id") int id, Model gmail) throws Exception {
        List<GmailMessage> messageList = gmailService.getListM();
        GmailMessage message = messageList.get(id);
        gmail.addAttribute("message", message);
        return "show";
    }

    @PostMapping
    public String sendM(@ModelAttribute("mail") Gmail gmail) throws MessagingException, IOException {
        System.out.println(gmail.getSubject() + " " + gmail.getMessage() + " " + gmail.getAttachment());
        gmailService.setGmail(gmail);

        if (gmail.getAttachment().equals("")) {
            gmailService.sendM();
        } else {
            gmailService.sendMwithAttachment();
        }
        return "redirect:/inbox";
    }
}
