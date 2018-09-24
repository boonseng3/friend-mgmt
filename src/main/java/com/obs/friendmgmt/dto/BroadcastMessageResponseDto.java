package com.obs.friendmgmt.dto;

import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class BroadcastMessageResponseDto {
    private boolean success;
    private List<String> recipients;
}
