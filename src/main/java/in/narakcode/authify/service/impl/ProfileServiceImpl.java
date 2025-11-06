package in.narakcode.authify.service.impl;

import in.narakcode.authify.dto.ProfileRequest;
import in.narakcode.authify.dto.ProfileResponse;
import in.narakcode.authify.entity.UserEntity;
import in.narakcode.authify.repository.UserRepository;
import in.narakcode.authify.service.EmailService;
import in.narakcode.authify.service.ProfileService;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final EmailService emailService;
  private static final Logger log = LoggerFactory.getLogger(ProfileServiceImpl.class);

  @Override
  public ProfileResponse getProfile(String email) {
    UserEntity existingUser = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    return convertToProfileResponse(existingUser);
  }

  @Transactional
  @Override
  public ProfileResponse createProfile(ProfileRequest request) {

    UserEntity newProfile = convertToUserEntity(request);

    if (!userRepository.existsByEmail(request.getEmail())) {
      newProfile = userRepository.save(newProfile);
      return convertToProfileResponse(newProfile);
    }
    throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
  }

  private ProfileResponse convertToProfileResponse(UserEntity newProfile) {
    return ProfileResponse.builder().name(newProfile.getName()).email(newProfile.getEmail())
        .userId(newProfile.getUserId()).isAccountVerified(newProfile.getIsAccountVerified()).build();
  }

  private UserEntity convertToUserEntity(ProfileRequest request) {
    return UserEntity.builder().email(request.getEmail()).userId(UUID.randomUUID().toString())
        .name(request.getName()).password(passwordEncoder.encode(request.getPassword()))
        .isAccountVerified(false).resetOtpExpireAt(0L).verifyOtp(null).verifyOtpExpireAt(0L)
        .resetOtp(null).build();
  }

  @Override
  public void sendResetOtp(String email) {
    UserEntity existingUser = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("No account found with this email address"));

    // Generate 6 digit OTP
    String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));

    // Calculate expiration time (current time + 15 minutes in milliseconds)
    long expirationTime = System.currentTimeMillis() + (15 * 60 * 1000);

    // Update the profile user
    existingUser.setResetOtp(otp);
    existingUser.setResetOtpExpireAt(expirationTime);

    // Save the updated user
    userRepository.save(existingUser);

    try {
      emailService.sendResetOtpEmail(email, otp);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
          "Failed to send email. Please try again later");
    }

  }

  @Transactional
  @Override
  public void resetPassword(String email, String otp, String newPassword) {
    UserEntity existingUser = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("No account found with this email address"));

    // Validate the provided OTP
    if (existingUser.getResetOtp() == null || !existingUser.getResetOtp().equals(otp)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP. Please check and try again");
    }

    // Check if the OTP has expired
    if (System.currentTimeMillis() > existingUser.getResetOtpExpireAt()) {
      // Invalidate the OTP
      existingUser.setResetOtp(null);
      existingUser.setResetOtpExpireAt(0L);
      userRepository.save(existingUser);

      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP has expired. Please request a new one");
    }

    // OTP is valid, proceed with password reset
    existingUser.setPassword(passwordEncoder.encode(newPassword));

    // Invalidate the OTP after successful reset
    existingUser.setResetOtp(null);
    existingUser.setResetOtpExpireAt(0L);
    userRepository.save(existingUser);

    // Send a confirmation email
    try {
      emailService.sendPasswordResetSuccessEmail(existingUser.getEmail(), existingUser.getName());

    } catch (Exception e) {
      log.warn("Unable to send password reset confirmation email to {}: {}", email, e.getMessage());
    }
  }

  @Override
  public void sendOtp(String email) {
    UserEntity existingUser = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            "No account found with this email address"));

    // If account is already verified, don't send OTP
    if (Boolean.TRUE.equals(existingUser.getIsAccountVerified())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your account is already verified");
    }

    // Generate 6 digit OTP
    String otp = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));

    // Calculate expiration time (current time + 15 minutes in milliseconds)
    long expirationTime = System.currentTimeMillis() + (15 * 60 * 1000);

    // Update the user entity
    existingUser.setVerifyOtp(otp);
    existingUser.setVerifyOtpExpireAt(expirationTime);

    userRepository.save(existingUser);

    try {
      emailService.sendOtpEmail(existingUser.getEmail(), otp);
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
          "Failed to send verification email. Please try again later");
    }
  }

  @Transactional
  @Override
  public void verifyOtp(String email, String otp) {
    UserEntity existingUser = userRepository.findByEmail(email)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
            "No account found with this email address"));

    // Check if already verified
    if (Boolean.TRUE.equals(existingUser.getIsAccountVerified())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your account is already verified");
    }

    // Validate the provided OTP
    if (existingUser.getVerifyOtp() == null || !existingUser.getVerifyOtp().equals(otp)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP. Please check and try again");
    }

    // Check if the OTP has expired
    if (System.currentTimeMillis() > existingUser.getVerifyOtpExpireAt()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP has expired. Please request a new one");
    }

    // OTP is valid, verify the account
    existingUser.setIsAccountVerified(true);
    existingUser.setVerifyOtp(null);
    existingUser.setVerifyOtpExpireAt(0L);
    userRepository.save(existingUser);

    // Send welcome email after successful verification
    try {
      emailService.sendWelcomeEmail(existingUser.getEmail(), existingUser.getName());
    } catch (Exception e) {
      log.warn("Unable to send welcome email to {}: {}", email, e.getMessage());
    }
  }

  @Override
  public String getLoggedInUserId(String email) {
    UserEntity existingUser = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    return existingUser.getUserId();
  }

}
