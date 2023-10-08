package com.user.aggregation.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@ToString
@Getter
public class OutputUserDetail {
    private Long userId;
    private String date;
    private int post;
    private int comment;
    private int likesReceived;
}
