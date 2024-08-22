package com.example.cakeapp.model;

import java.util.List;

public class SanPhamMoiModel {
    boolean success;
    String message;
    List<SanPhamMoi> resulst;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<SanPhamMoi> getResulst() {
        return resulst;
    }

    public void setResulst(List<SanPhamMoi> resulst) {
        this.resulst = resulst;
    }
}
