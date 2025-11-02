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
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

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
        .userId(newProfile.getUserId()).isAccountVerified(newProfile.isAccountVerified()).build();
  }

  private UserEntity convertToUserEntity(ProfileRequest request) {
    return UserEntity.builder().email(request.getEmail()).userId(UUID.randomUUID().toString())
        .name(request.getName()).password(passwordEncoder.encode(request.getPassword()))
        .isAccountVerified(true).resetOtpExpireAt(0L).verifyOtp(null).verifyOtpExpireAt(0L)
        .resetOtp(null).build();
  }

  @Override
  public void sendResetOtp(String email) {
    UserEntity existingUser = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

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
      throw new RuntimeException("Unable to send mail");
    }

  }

  @Transactional
  @Override
  public void resetPassword(String email, String otp, String newPassword) {
    UserEntity existingUser = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found" + email));

    // Validate the provided OTP
    if (existingUser.getResetOtp() == null || !existingUser.getResetOtp().equals(otp)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid OTP");
    }

    // Chcek if the OTP has expired
    if (System.currentTimeMillis() > existingUser.getResetOtpExpireAt()) {
      // Invalidate the OTP
      existingUser.setResetOtp(otp);
      existingUser.setResetOtpExpireAt(0L);
      userRepository.save(existingUser);

      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "OTP has expired");
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

}
