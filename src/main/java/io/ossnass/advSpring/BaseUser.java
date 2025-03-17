package io.ossnass.advSpring;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Objects;

@MappedSuperclass
@Getter
@Setter
@Accessors(chain = true)
public class BaseUser<ID> extends Deletable {
    private static final long serialVersionUID = 1L;


    @Id
    @Column(name = "id", nullable = false)
    private ID id;


    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "password", nullable = false, length = 2147483647)
    private String password;

    @Column(name = "account_locked")
    private Boolean accountLocked;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "account_expired", nullable = false)
    private boolean accountExpired;

    @Column(name = "credential_expired", nullable = false)
    private boolean credentialExpired;

    @Column(name = "first_login", nullable = false)
    private boolean firstLogin;

    @Column(name = "department_id")
    private Integer departmentId;

    @Column(name = "reset_token", length = 255)
    private String resetToken;

    @Column(name = "reset_token_creation_time")
    private LocalDateTime resetTokenCreationTime;

    @Column(name = "login_id", nullable = false, length = 20)
    private String loginId;

    @Column(name = "login_id_normalized", nullable = false, length = 20)
    private String loginIdNormalized;

    @Column(name = "deleted")
    private Boolean deleted;


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseUser val = (BaseUser) o;
        return Objects.equals(id, val.id) && Objects.equals(firstName, val.firstName) && Objects.equals(lastName, val.lastName) && Objects.equals(password, val.password) && Objects.equals(accountLocked, val.accountLocked) && Objects.equals(enabled, val.enabled) && Objects.equals(accountExpired, val.accountExpired) && Objects.equals(credentialExpired, val.credentialExpired) && Objects.equals(firstLogin, val.firstLogin) && Objects.equals(departmentId, val.departmentId) && Objects.equals(resetToken, val.resetToken) && Objects.equals(resetTokenCreationTime, val.resetTokenCreationTime) && Objects.equals(loginId, val.loginId) && Objects.equals(loginIdNormalized, val.loginIdNormalized) && Objects.equals(deleted, val.deleted);
    }


    public int hashCode() {
        return Objects.hash(
                id, firstName, lastName, password, accountLocked, enabled, accountExpired, credentialExpired, firstLogin, departmentId, resetToken, resetTokenCreationTime, loginId, loginIdNormalized, deleted);
    }

}
