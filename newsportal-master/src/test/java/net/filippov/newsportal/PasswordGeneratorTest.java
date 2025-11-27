package net.filippov.newsportal;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGeneratorTest {
    @Test
    public void generateHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println("GENERATED_HASH_START");
        System.out.println(encoder.encode("admin"));
        System.out.println("GENERATED_HASH_END");
    }
}
