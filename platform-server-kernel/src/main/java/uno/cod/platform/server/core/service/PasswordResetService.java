package uno.cod.platform.server.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uno.cod.platform.server.core.domain.PasswordResetToken;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.exception.CodunoIllegalArgumentException;
import uno.cod.platform.server.core.repository.PasswordResetTokenRepository;
import uno.cod.platform.server.core.repository.UserRepository;
import uno.cod.platform.server.core.service.mail.MailService;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Service
@Transactional
public class PasswordResetService {
    private static final Logger LOG = LoggerFactory.getLogger(PasswordResetService.class);

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final HttpSession httpSession;
    private SecureRandom random = new SecureRandom();

    @Value("#{T(java.time.Duration).parse('${coduno.password_reset.expire}')}")
    private Duration duration;

    @Autowired
    public PasswordResetService(MailService mailService,
                                UserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                PasswordResetTokenRepository passwordResetTokenRepository,
                                HttpSession httpSession
    ) {
        this.mailService = mailService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.httpSession = httpSession;
    }

    public void sendResetTokenMail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return;
        }

        String token = new BigInteger(130, random).toString(32);

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setUser(user);
        passwordResetToken.setExpire(ZonedDateTime.now().plus(duration));

        passwordResetTokenRepository.save(passwordResetToken);

        Map<String, Object> params = new HashMap<>();
        params.put("name", user.getUsername());
        params.put("token", token);

        try {
            mailService.sendMail(user.getUsername(), user.getEmail(), "Reset your Coduno password", "password-reset.html", params, Locale.ENGLISH);
        } catch (MessagingException e) {
            LOG.error("password reset mail could not be sent", e);
        }
    }

    public void resetPassword(String token, String password) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findOne(token);
        if (passwordResetToken == null) {
            throw new CodunoIllegalArgumentException("");
        }
        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        /* authenticate */
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        httpSession.setAttribute(SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

        passwordResetTokenRepository.delete(passwordResetToken);
    }

    @Scheduled(fixedRate = 5000)
    public void cleanupTokens() {
        passwordResetTokenRepository.deleteExpiredTokens(ZonedDateTime.now());
    }
}
