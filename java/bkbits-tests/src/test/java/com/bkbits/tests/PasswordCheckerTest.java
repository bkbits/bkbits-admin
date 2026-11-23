package com.bkbits.tests;

import com.bkbits.encrypt.PasswordChecker;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 密码校验器测试。
 */
public class PasswordCheckerTest {

    @Test
    public void testLength() {
        PasswordChecker checker = PasswordChecker.builder().build();
        assertFalse(checker.check("abc").isOk());
        assertEquals("密码长度不能小于 6", checker.check("abc").getMessage());
        assertFalse(checker.check("a".repeat(21)).isOk());
        assertTrue(checker.check("abcdef").isOk());
    }

    @Test
    public void testRequiredClasses() {
        PasswordChecker checker = PasswordChecker.builder()
                .needUpperCase(true)
                .needNumber(true)
                .build();
        assertFalse(checker.check("abcdef").isOk());
        assertTrue(checker.check("Abcdef1").isOk());
        assertNull(checker.check("Abcdef1").getMessage());
    }

    @Test
    public void testComplexity() {
        PasswordChecker checker = PasswordChecker.builder().complex(3).build();
        assertFalse(checker.check("abcdef1").isOk());
        assertTrue(checker.check("Abcdef1").isOk());
    }

    @Test
    public void testIllegalCharsAndSpace() {
        PasswordChecker checker = PasswordChecker.builder().illegalChars(new char[]{'@'}).build();
        assertFalse(checker.check("abc@def").isOk());

        PasswordChecker noSpace = PasswordChecker.builder().supportSpaceChar(false).build();
        assertFalse(noSpace.check("abc def").isOk());
        assertTrue(noSpace.check("abcdef").isOk());
    }
}
