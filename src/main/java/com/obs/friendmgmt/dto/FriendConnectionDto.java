package com.obs.friendmgmt.dto;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FriendConnectionDto implements Serializable {
    private List<String> friends;
    private boolean success;
    private int count;

}