package org.atechtrade.rent.dto;

import org.atechtrade.rent.model.BaseEntity;

public record ApiResponse(Boolean success, String message, Object data) {
    public static ApiResponse ok(final String message) {
        return ok(message, null);
    }
    public static ApiResponse ok(final Object data) {
        return ok(null, data);
    }
    public static ApiResponse ok(final String message, final BaseEntity entity) {
        return new ApiResponse(true, message, entity != null && entity.getId() != null ? entity.getId() : null);
    }
    public static ApiResponse ok(final String message, final Object data) {
        return new ApiResponse(true, message, data);
    }
    public static ApiResponse err(final String message) {
        return new ApiResponse(false, message, null);
    }
}