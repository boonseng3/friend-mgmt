package com.obs.friendmgmt.exception;

import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FieldErrorMessage {
    private String field;
    private String message;
}
