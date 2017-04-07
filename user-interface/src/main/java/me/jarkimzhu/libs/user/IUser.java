package me.jarkimzhu.libs.user;

/**
 * @author JarkimZhu
 *         Created on 2017/1/24.
 * @since jdk1.8
 */
public interface IUser {
    String getUserName();

    void setUserName(String userName);

    String getRealName();

    void setRealName(String realName);

    String getPassword();

    void setPassword(String password);

    String getMobile();

    void setMobile(String mobile);

    String getEmail();

    void setEmail(String email);
}
