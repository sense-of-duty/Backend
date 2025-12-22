package org.example.gdgpage.service.attendance;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class AttendanceCodeGeneratorImpl implements AttendanceCodeGenerator {

    private final SecureRandom random = new SecureRandom();

    @Override
    public String generate3Digits() {
        int code = random.nextInt(1000); // 0..999
        return String.format("%03d", code);
    }
}
