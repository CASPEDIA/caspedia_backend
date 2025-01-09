package com.cast.caspedia.security.custom;

import com.cast.caspedia.user.domain.User;
import com.cast.caspedia.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findUserById(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        if(!user.isEnabled()) {
            throw new UsernameNotFoundException("User is not enabled");
        }

        return new CustomUserDetails(user);
    }
}
