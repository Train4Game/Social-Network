package com.train4game.social.util;

import com.train4game.social.model.User;
import com.train4game.social.to.RegisterUserTo;
import com.train4game.social.to.UserSettingsTo;
import com.train4game.social.to.UserTo;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

public class UserUtil {
    public static User createNewFromTo(RegisterUserTo userTo) {
        return new User(null, userTo.getName(), userTo.getSurname(), userTo.getEmail(), userTo.getPassword(), User.Role.ROLE_USER);
    }

    public static UserTo asTo(User user) {
        return new UserTo(user.getId(), user.getName(), user.getSurname(), user.getEmail(), user.getPassword(), user.getLocale(), user.getVkId());
    }

    public static User prepareToSave(User user, PasswordEncoder passwordEncoder) {
        String password = user.getPassword();
        user.setPassword(StringUtils.hasText(password) ? passwordEncoder.encode(password) : password);
        user.setEmail(user.getEmail().toLowerCase());
        return user;
    }

    public static UserSettingsTo asSettings(User user) {
        return new UserSettingsTo(user.getLocale());
    }

    public static String generatePassword() {
        return RandomStringUtils.random(18, 0, 123, true, true, null);
    }

    public static User createUserFromGoogleMap(Map<String, Object> googleMap, String password) {
        User user = new User();
        user.setName((String) googleMap.get("given_name"));
        user.setSurname((String) googleMap.get("family_name"));
        user.setLocale((String) googleMap.get("locale"));
        user.setEmail((String) googleMap.get("email"));
        user.setGoogleId((String) googleMap.get("sub"));
        user.setEnabled(true);
        user.setRoles(EnumSet.of(User.Role.ROLE_USER));
        user.setPassword(password);
        return user;
    }

    public static User createUserFromFacebookMap(Map<String, Object> facebookMap, String password) {
        User user = new User();
        user.setName((String) facebookMap.get("first_name"));
        user.setSurname((String) facebookMap.get("last_name"));
        user.setEmail((String) facebookMap.get("email"));
        user.setFacebookId((String) facebookMap.get("id"));
        user.setEnabled(true);
        user.setRoles(EnumSet.of(User.Role.ROLE_USER));
        user.setPassword(password);
        return user;
    }

    public static User createUserFromVkMap(Map<String, Object> vkMap, String password) {
        User user = new User();
        user.setName((String) vkMap.get("first_name"));
        user.setSurname((String) vkMap.get("last_name"));
        user.setEnabled(true);
        user.setRoles(EnumSet.of(User.Role.ROLE_USER));
        user.setEmail((String) vkMap.get("email"));
        user.setPassword(password);
        return user;
    }

    public static User updateFromTo(User user, UserTo userTo) {
        user.setName(userTo.getName());
        user.setSurname(userTo.getSurname());
        user.setEmail(userTo.getEmail());
        user.setPassword(userTo.getPassword());
        user.setLocale(userTo.getLocale());
        return user;
    }
}
