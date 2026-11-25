package com.bkbits.encrypt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PasswordCheckResult {
    private boolean ok;
    private String message;
}
