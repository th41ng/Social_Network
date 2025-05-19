/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.socialapp.dto;  // Đảm bảo package đúng với `PostDTO`

public class UserDTO {
    private String fullName;
    private String avatar;

    // Constructor
    public UserDTO(String fullName, String avatar) {
        this.fullName = fullName;
        this.avatar = avatar;
    }

    // Getters and Setters
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}