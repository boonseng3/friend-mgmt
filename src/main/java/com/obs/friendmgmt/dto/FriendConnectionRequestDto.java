package com.obs.friendmgmt.dto;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FriendConnectionRequestDto implements Serializable {
    private String email;
}
