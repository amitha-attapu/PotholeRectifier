package com.major.potholerectifier.model;

import java.util.Date;

public class UserDetails {

    private long userId;

    private String username;

    private String password;

    private String phone;

    private UserRole userRole;

    private Date createdAt;

    public UserDetails() {
    }

    public UserDetails(String username, String password, String phone, UserRole userRole) {
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.userRole = userRole;
    }

    public UserDetails(long userId, String username, String password, String phone, UserRole userRole, Date createdAt) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.userRole = userRole;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "UserDetails{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", userRole=" + userRole +
                ", createdAt=" + createdAt +
                '}';
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
