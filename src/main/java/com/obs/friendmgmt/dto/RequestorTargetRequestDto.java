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
public class RequestorTargetRequestDto implements Serializable {
    private String requestor;
    private String target;
}
