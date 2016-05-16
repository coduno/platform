package uno.cod.platform.server.core.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.repository.UserRepository;


@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user;
        if (username.contains("@")) {
            user = userRepository.findByEmail(username);
        } else {
            user = userRepository.findByUsernameValue(username);
        }

        if (user == null) {
            throw new UsernameNotFoundException("user not registered yet");
        }

        return user;
    }
}
