package com.obs.friendmgmt.exception;

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
public class ErrorMessageDto {
    private String message;
    private String code;
    private List<FieldErrorMessage> detail;


}