package com.train4game.social.service;

import com.train4game.social.model.Recaptcha;
import com.train4game.social.addons.recaptcha.ReCaptchaCacheService;
import com.train4game.social.addons.recaptcha.ReCaptchaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestOperations;

import javax.servlet.http.HttpServletRequest;

@Service
public class RecaptchaService {
    private static final String RECAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify";
    private final Logger log = LoggerFactory.getLogger(RecaptchaService.class);
    private ReCaptchaCacheService cacheService = new ReCaptchaCacheService();

    @Value("${google.recaptcha.secret}")
    private String recaptchaSecret;

    @Value("${google.recaptcha.html}")
    private String recaptchaHtml;

    @Autowired
    private RestOperations restOperations;

    @Autowired
    private HttpServletRequest req;

    public Recaptcha verifyRecaptcha(String recaptchaResponse) {
        String ip = req.getRemoteAddr();
        if (cacheService.isBlocked(ip)) {
            log.info("User with ip {} exceeded maximum tries", ip);
            throw new ReCaptchaException("Client exceeded maximum number of failed attempts");
        }

        String url = RECAPTCHA_URL + String.format("?secret=%s&response=%s&remoteip=%s", recaptchaSecret, recaptchaResponse, ip);
        Recaptcha recaptcha = restOperations.getForObject(url, Recaptcha.class);

        if (recaptcha != null && !recaptcha.isSuccess() && recaptcha.hasClientError()) {
            log.info("Recaptcha for ip {} failed", ip);
            cacheService.reCaptchaFailed(ip);
        } else {
            log.info("Recaptcha for ip {} succeeded", ip);
            cacheService.reCaptchaSucceeded(ip);
        }

        return recaptcha;
    }

    public boolean isVerifyRecaptcha(String recaptchaResponse) {
        return verifyRecaptcha(recaptchaResponse).isSuccess();
    }

    public String getRecaptchaHtml() {
        return recaptchaHtml;
    }
}
