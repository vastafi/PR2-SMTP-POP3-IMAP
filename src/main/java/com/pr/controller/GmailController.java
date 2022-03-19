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
    private final GmailService mailService;

    public GmailController(GmailService mailService) {
        this.mailService = mailService;
    }

    @GetMapping
    public String newM(Model mail) {
        mail.addAttribute("mail", new Gmail());
        return "send";
    }

    @GetMapping("inbox")
    public String showInbox(Model mail) throws Exception {

        List<GmailMessage> messageList = mailService.getListM();
        mail.addAttribute("messageList", messageList);
        return "inbox";
    }

    @GetMapping("inbox/message/{id}")
    public String showM(@PathVariable(value = "id") int id, Model mail) throws Exception {
        List<GmailMessage> messageList = mailService.getListM();
        GmailMessage message = messageList.get(id);
        mail.addAttribute("message", message);
        return "show";
    }

    @PostMapping
    public String sendMessage(@ModelAttribute("mail") Gmail mail) throws MessagingException, IOException {
        System.out.println(mail.getSubject() + " " + mail.getMessage() + " " + mail.getAttachment());
        mailService.setGmail(mail);

        if (mail.getAttachment().equals("")) {
            mailService.sendM();
        } else {
            mailService.sendMwithAttachment();
        }
        return "redirect:/inbox";
    }
}
