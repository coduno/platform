package uno.cod.platform.server.core.social;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.stereotype.Service;
import uno.cod.platform.server.core.repository.UserRepository;

import java.util.UUID;

@Service
public class SimpleSocialUserDetailsService implements SocialUserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public SimpleSocialUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException, DataAccessException {
        try {
            UUID uuid = UUID.fromString(userId);
            UserDetails userDetails = userRepository.findOne(uuid);
            if (userDetails == null) {
                throw new UsernameNotFoundException("no user with that id found");
            }
            return (SocialUserDetails) userDetails;
        } catch (IllegalArgumentException e) {
            throw new UsernameNotFoundException("invalid user id");
        }
    }
}
