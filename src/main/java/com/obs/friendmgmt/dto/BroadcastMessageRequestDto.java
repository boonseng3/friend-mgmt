package com.obs.friendmgmt.dto;

import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class BroadcastMessageRequestDto {
    private String sender;
    private String text;
}
