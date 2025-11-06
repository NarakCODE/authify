package in.narakcode.authify.service;

import in.narakcode.authify.entity.UserEntity;
import in.narakcode.authify.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    UserEntity existingUser = userRepository.findByEmail(email).orElseThrow(
        () -> new UsernameNotFoundException("Invalid email or password"));

    // Check if account is verified - prevent login if not verified
    boolean isEnabled = Boolean.TRUE.equals(existingUser.getIsAccountVerified());

    return new User(
        existingUser.getEmail(),
        existingUser.getPassword(),
        isEnabled, // enabled only if account is verified
        true, // accountNonExpired
        true, // credentialsNonExpired
        true, // accountNonLocked
        List.of(new SimpleGrantedAuthority("ROLE_USER")) // must have at least one authority
    );

  }

}