package com.cast.caspedia.user.dto;

public class IsAdminResponseDto {
    private boolean isAdmin;

    public IsAdminResponseDto(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
