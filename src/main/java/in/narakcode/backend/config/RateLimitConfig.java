package in.narakcode.backend.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RateLimitConfig {

    @Value("${rate.limit.login.capacity:5}")
    private int loginCapacity;

    @Value("${rate.limit.login.refill.tokens:5}")
    private int loginRefillTokens;

    @Value("${rate.limit.login.refill.minutes:15}")
    private int loginRefillMinutes;

    @Value("${rate.limit.otp.capacity:3}")
    private int otpCapacity;

    @Value("${rate.limit.otp.refill.tokens:3}")
    private int otpRefillTokens;

    @Value("${rate.limit.otp.refill.minutes:10}")
    private int otpRefillMinutes;

    @Value("${rate.limit.general.capacity:100}")
    private int generalCapacity;

    @Value("${rate.limit.general.refill.tokens:100}")
    private int generalRefillTokens;

    @Value("${rate.limit.general.refill.minutes:1}")
    private int generalRefillMinutes;

    private final Map<String, Bucket> loginBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> otpBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> generalBuckets = new ConcurrentHashMap<>();

    public Bucket resolveLoginBucket(String key) {
        return loginBuckets.computeIfAbsent(key, k -> createLoginBucket());
    }

    public Bucket resolveOtpBucket(String key) {
        return otpBuckets.computeIfAbsent(key, k -> createOtpBucket());
    }

    public Bucket resolveGeneralBucket(String key) {
        return generalBuckets.computeIfAbsent(key, k -> createGeneralBucket());
    }

    private Bucket createLoginBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(loginCapacity)
                .refillIntervally(loginRefillTokens, Duration.ofMinutes(loginRefillMinutes))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    private Bucket createOtpBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(otpCapacity)
                .refillIntervally(otpRefillTokens, Duration.ofMinutes(otpRefillMinutes))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    private Bucket createGeneralBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(generalCapacity)
                .refillIntervally(generalRefillTokens, Duration.ofMinutes(generalRefillMinutes))
                .build();
        return Bucket.builder().addLimit(limit).build();
    }
}
