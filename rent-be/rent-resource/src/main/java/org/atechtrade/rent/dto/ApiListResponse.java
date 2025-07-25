package org.atechtrade.rent.dto;

import java.util.List;

public record ApiListResponse<T>(List<T> results, long totalCount) {
	public static <T> ApiListResponse<T> ok(List<T> results) {
		return new ApiListResponse<>(results, results != null ? results.size() : 0);
	}
}
