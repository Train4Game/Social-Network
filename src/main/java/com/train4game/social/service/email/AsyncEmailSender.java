package com.train4game.social.service.email;

import com.train4game.social.MessageUtil;
import com.train4game.social.model.Token;
import com.train4game.social.model.User;
import com.train4game.social.service.EmailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
@AllArgsConstructor
@Slf4j
public class AsyncEmailSender {
    private EmailService emailService;
    private MessageUtil messages;

    @Async("threadPoolTaskExecutor")
    public void sendTokenEmail(Token token, String prefix, String url, Locale locale) {
        User user = token.getUser();
        String title = messages.getMessage(prefix + "title", locale);
        Map<String, Object> model = new HashMap<>();
        model.put("title", title);
        model.put("text", messages.getMessage(prefix + "text", locale, user.getName()));
        model.put("link", url + "?token=" + token.getToken());
        model.put("btnText", messages.getMessage(prefix + "btnText", locale));
        Email email = new Email("SocialNetwork", user.getEmail(), title, "email/email-template", model);

        try {
            emailService.sendEmail(email);
        } catch (MessagingException e) {
            log.error("Can't send email for user with email {}", token.getUser().getEmail(), e);
        }
    }

    @Async("threadPoolTaskExecutor")
    public void sendOAuthEmail(String email, String password, String name, String oauth) {
        String title = messages.getMessage("email.oauth.password.title");

        HashMap<String, Object> model = new HashMap<>();
        model.put("title", title);
        model.put("text", messages.getMessage("email.oauth.password.text", name, oauth));
        model.put("password", messages.getMessage("email.oauth.password.password", password));
        model.put("subtext", messages.getMessage("email.oauth.password.subtext", email));

        Email newEmail = new Email("SocialNetwork", email, title, "email/oauth-password", model);

        try {
            emailService.sendEmail(newEmail);
        } catch (MessagingException e) {
            log.error("Can't send email for user with email {}", email, e);
        }
    }
}
