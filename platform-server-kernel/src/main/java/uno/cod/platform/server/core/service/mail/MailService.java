package uno.cod.platform.server.core.service.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Locale;
import java.util.Map;

@Service
public class MailService {
    private final SpringTemplateEngine templateEngine;
    private final JavaMailSender mailSender;
    @Value("${coduno.url}")
    private String url;
    @Value("${coduno.mail.from}")
    private String fromMail;

    @Autowired
    public MailService(SpringTemplateEngine templateEngine, JavaMailSender mailSender) {
        this.templateEngine = templateEngine;
        this.mailSender = mailSender;
    }

    @Async
    public void sendMail(final String recipientName,
                         final String recipientEmail,
                         final String subject,
                         final String template,
                         final Map<String, Object> attributes,
                         final Locale locale)
            throws MessagingException {

        // Prepare the evaluation context
        final Context ctx = new Context(locale);
        ctx.setVariable("name", recipientName);
        ctx.setVariable("url", url);
        for (String key : attributes.keySet()) {
            ctx.setVariable(key, attributes.get(key));
        }

        // Prepare message using a Spring helper
        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message =
                new MimeMessageHelper(mimeMessage, true, "UTF-8"); // true = multipart
        message.setSubject(subject);
        message.setFrom(fromMail);
        message.setTo(recipientEmail);

        // Create the HTML body using Thymeleaf
        // TODO: Fix that '&' signs can be in mails without replacing, see
        // https://github.com/coduno/platform/pull/182/files#diff-837c6ec864b6a87765e5d4365df669adR58
        final String htmlContent = this.templateEngine.process(template, ctx).replaceAll("&amp;", "&");
        message.setText(htmlContent, true); // true = isHtml

        // Send mail
        this.mailSender.send(mimeMessage);
    }
}
