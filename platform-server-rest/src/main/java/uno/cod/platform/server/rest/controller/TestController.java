package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uno.cod.platform.server.core.service.MailService;

import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.Locale;

@RestController
public class TestController {

    @Autowired
    private MailService mailService;

    @RequestMapping(name = "/test")
    public void test() throws MessagingException {
        mailService.sendMail("WAT", "jakob611@yahoo.de", "wat", "test.html", new HashMap<>(), Locale.ENGLISH);
    }
}
