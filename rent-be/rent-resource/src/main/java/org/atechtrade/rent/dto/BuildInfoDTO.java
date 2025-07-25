package org.atechtrade.rent.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.info.BuildProperties;
import org.springframework.util.StringUtils;

import java.time.Instant;

@Getter
@Setter
@Builder
public class BuildInfoDTO {

    private String group;
    private String artifact;
    private String name;
    private String version;
    private Instant time;

    private String gitBranch;
    private String gitCommitId;
    private String gitCommitTime;

    private static final String GIT_BRANCH = "git.branch";
    private static final String GIT_COMMIT_ID = "git.commit.id";
    private static final String GIT_COMMIT_TIME = "git.commit.time";

    public static BuildInfoDTO of(final BuildProperties bp) {
        BuildInfoDTO dto = BuildInfoDTO.builder().build();
        BeanUtils.copyProperties(bp, dto);
        if (StringUtils.hasText(bp.get(GIT_COMMIT_ID)) && !"N/A".equals(bp.get(GIT_COMMIT_ID))) {
            dto.setGitCommitId(bp.get(GIT_COMMIT_ID));
        }
        if (StringUtils.hasText(bp.get(GIT_COMMIT_TIME)) && !"N/A".equals(bp.get(GIT_COMMIT_TIME))) {
            dto.setGitCommitTime(bp.get(GIT_COMMIT_TIME));
        }
        if (StringUtils.hasText(bp.get(GIT_BRANCH)) && !"N/A".equals(bp.get(GIT_BRANCH))) {
            dto.setGitBranch(bp.get(GIT_BRANCH));
        }
        return dto;
    }
}
