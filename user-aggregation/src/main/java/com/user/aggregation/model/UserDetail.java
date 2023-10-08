package com.user.aggregation.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@ToString
@Getter
@Setter
public class UserDetail {
    private String userId;
    private String eventName;
    private String timestamp;
    private String formattedDate;
}
