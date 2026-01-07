package com.kinapi.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FamilyGroupDetailDto implements Serializable {
    @JsonProperty("family_group_name")
    private String familyGroupName;
    @JsonProperty("family_group_id")
    private String familyGroupId;
    @JsonProperty("invitation_code")
    private String invitationCode;
    @JsonProperty("reset_time")
    private String resetTime;
    @JsonProperty("family_member")
    private List<FamilyMember> familyMemberList;

    @Builder
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FamilyMember implements Serializable {
        @JsonProperty("id")
        private String id;
        @JsonProperty("name")
        private String name;
        @JsonProperty("email")
        private String email;
        @JsonProperty("dob")
        private String dob;
        @JsonProperty("role")
        private String role;
        @JsonProperty("group_creator")
        private Boolean groupCreator;
        @JsonProperty("avatar_url")
        private String avatarUrl;
    }
}
