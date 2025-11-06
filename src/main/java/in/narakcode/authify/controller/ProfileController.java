package in.narakcode.authify.controller;

import in.narakcode.authify.dto.ProfileRequest;
import in.narakcode.authify.dto.ProfileResponse;
import in.narakcode.authify.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse register(@Valid @RequestBody ProfileRequest request) {
        ProfileResponse response = profileService.createProfile(request);

        // Send verification OTP immediately after registration
        profileService.sendOtp(request.getEmail());

        return response;
    }

    @GetMapping("/test")
    public String test() {
        return "Hello world";
    }

    @GetMapping("/profile")
    public ProfileResponse getProfile(@CurrentSecurityContext(expression = "authentication?.name") String email) {
        return profileService.getProfile(email);
    }

    @PostMapping("/verify-account")
    public ResponseEntity<String> verifyAccount(
            @Valid @RequestBody in.narakcode.authify.dto.VerifyAccountRequest request) {
        profileService.verifyOtp(request.getEmail(), request.getOtp());
        return ResponseEntity.ok("Account verified successfully. You can now login");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@CurrentSecurityContext(expression = "authentication?.name") String email,
            @RequestParam String otp) {
        profileService.verifyOtp(email, otp);
        return ResponseEntity.ok("Account verified successfully");
    }

}
