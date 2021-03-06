package com.train4game.social.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.train4game.social.HasId;
import org.hibernate.annotations.BatchSize;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.EnumSet;
import java.util.Set;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email", name = "users_unique_email_idx"))
public class User extends AbstractNamedEntity {
    @NotBlank
    @Size(min = 2, max = 100)
    @Column(name = "surname", nullable = false)
    private String surname;

    @Email
    @NotBlank
    @Size(max = 200)
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    @NotBlank
    @Size(min = 5, max = 100)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "registered", nullable = false, columnDefinition = "TIMESTAMP DEFAULT now()")
    @NotNull
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Date registered = new Date();

    @Column(name = "enabled", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean enabled = false;

    @Column(name = "locale", nullable = false, columnDefinition = "VARCHAR DEFAULT 'en'")
    private String locale = "en";

    @Column(name = "google_id")
    private String googleId;

    @Column(name = "facebook_id")
    private String facebookId;

    @Column(name = "vk_id")
    private Integer vkId;

    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @ElementCollection(fetch = FetchType.EAGER)
    @BatchSize(size = 200)
    private Set<Role> roles;

    public enum Role implements GrantedAuthority {
        ROLE_USER, ROLE_ADMIN;

        @Override
        public String getAuthority() {
            return name();
        }
    }

    public User() {
    }

    public User(User u) {
        this(u.getId(), u.getName(), u.getSurname(), u.getEmail(), u.getPassword(), u.getRegistered(), u.isEnabled(), u.getRoles());
    }

    public User(Integer id, String name, String surname, String email, String password, Role role, Role... roles) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.roles = EnumSet.of(role, roles);
    }

    public User(Integer id, String name, String surname, String email, String password, boolean enabled, Role role, Role... roles) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
        this.roles = EnumSet.of(role, roles);
    }

    public User(Integer id, String name, String surname, String email, String password, Date registered, boolean enabled, Set<Role> roles) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.registered = registered;
        this.enabled = enabled;
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getRegistered() {
        return registered;
    }

    public void setRegistered(Date registered) {
        this.registered = registered;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public Integer getVkId() {
        return vkId;
    }

    public void setVkId(Integer vkId) {
        this.vkId = vkId;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", registered=" + registered +
                ", enabled=" + enabled +
                ", roles=" + roles +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", id=" + id +
                '}';
    }
}
