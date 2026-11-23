package com.bkbits.encrypt;

import org.jetbrains.annotations.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@AllArgsConstructor
@Builder
public class PasswordChecker {
    @Builder.Default
    private int complex = 0; //复杂度
    @Builder.Default
    private boolean needLowerCase = false; //需要小写
    @Builder.Default
    private boolean needUpperCase = false; //需要大写
    @Builder.Default
    private boolean needNumber = false; // 需要数字
    @Builder.Default
    private boolean needSpecial = false; //需要特殊符号
    @Builder.Default
    private int minLength = 6; //最小长度
    @Builder.Default
    private int maxLength = 20; //最大长度
    @Builder.Default
    private boolean supportSpaceChar = true; //支持空白字符
    @Builder.Default
    private char[] illegalChars = new char[0]; //非法字符集合

    /**
     * 校验密码，返回第一个不满足的规则；全部通过时 {@code ok=true}、{@code message=null}。
     */
    public PasswordCheckResult check(@NotNull String password) {
        if (password.isEmpty()) {
            return fail("密码不能为空");
        }
        if (password.length() < minLength) {
            return fail("密码长度不能小于 " + minLength);
        }
        if (password.length() > maxLength) {
            return fail("密码长度不能大于 " + maxLength);
        }
        boolean lower = false;
        boolean upper = false;
        boolean number = false;
        boolean special = false;
        for (char ch : password.toCharArray()) {
            if (!isLegalChar(ch)) {
                return fail("密码包含非法字符：" + ch);
            }
            if (!supportSpaceChar && Character.isWhitespace(ch)) {
                return fail("密码不能包含空白字符");
            }
            if (Character.isLowerCase(ch)) {
                lower = true;
            } else if (Character.isUpperCase(ch)) {
                upper = true;
            } else if (Character.isDigit(ch)) {
                number = true;
            } else if (!Character.isWhitespace(ch)) {
                special = true;
            }
        }
        if (needLowerCase && !lower) {
            return fail("密码必须包含小写字母");
        }
        if (needUpperCase && !upper) {
            return fail("密码必须包含大写字母");
        }
        if (needNumber && !number) {
            return fail("密码必须包含数字");
        }
        if (needSpecial && !special) {
            return fail("密码必须包含特殊符号");
        }
        int classes = (lower ? 1 : 0) + (upper ? 1 : 0) + (number ? 1 : 0) + (special ? 1 : 0);
        if (classes < complex) {
            return fail("密码复杂度不足，至少需要 " + complex + " 种字符");
        }
        return ok();
    }

    private static PasswordCheckResult ok() {
        return new PasswordCheckResult(true, null);
    }

    private static PasswordCheckResult fail(String message) {
        return new PasswordCheckResult(false, message);
    }

    private boolean isLegalChar(char ch) {
        for (char illegalCh : illegalChars) {
            if (illegalCh == ch) {
                return false;
            }
        }
        return true;
    }
}
