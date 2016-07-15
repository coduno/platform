package uno.cod.platform.server.core.social;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.github.api.GitHub;
import org.springframework.social.google.api.Google;
import org.springframework.social.linkedin.api.LinkedIn;
import uno.cod.platform.server.core.config.social.GitHubEmail;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.repository.UserRepository;
import uno.cod.platform.server.core.util.UsernameUtil;

import java.text.Normalizer;

public class AccountConnectionSignUp implements ConnectionSignUp {
    private final UserRepository userRepository;

    public AccountConnectionSignUp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String execute(Connection<?> connection) {
        String email = getEmail(connection);
        User found = userRepository.findByEmail(email);
        if (found != null) {
            return found.getUserId();
        }
        // abort if email is null
        if (email == null) {
            return null;
        }

        User user = new User();
        user.setEmail(email);
        user.setUsername(determineUsername(connection));
        user.setFirstName(connection.fetchUserProfile().getFirstName());
        user.setLastName(connection.fetchUserProfile().getLastName());
        user.setPassword("");
        user.setEnabled(true);
        userRepository.save(user);

        return user.getUserId();
    }

    private String getEmail(Connection<?> connection) {
        Object api = connection.getApi();
        String email = connection.fetchUserProfile().getEmail();
        if (email == null && api instanceof GitHub) {
            GitHub github = (GitHub) api;
            GitHubEmail[] ghEmails = github.restOperations().getForObject("https://api.github.com/user/emails", GitHubEmail[].class);
            for (GitHubEmail ghEmail : ghEmails) {
                if (ghEmail.isPrimary()) {
                    email = ghEmail.getEmail();
                }
            }
        } else if (email == null && api instanceof Facebook) {
            // Facebook does not gurantee any email, since they introduced phone signup
            Facebook facebook = (Facebook) api;
            email = "id." + facebook.userOperations().getUserProfile().getId() + "@facebook-id.com";
        }
        return email;
    }

    private String determineUsername(Connection<?> connection) {
        String username = null;
        UserProfile userProfile = connection.fetchUserProfile();
        // First try the API of our social provider
        // works for github and google at least, but google
        // just returns a 21 digit number
        if (userProfile.getUsername() != null) {
            username = connection.fetchUserProfile().getUsername();
        }
        Object api = connection.getApi();
        if (api instanceof Google || api instanceof Facebook || api instanceof LinkedIn) {
            username = usernameFromRealname(userProfile);
        }

        if (username == null || username.isEmpty()) {
            UsernameUtil.randomUsername();
        }

        if (userRepository.findByCanonicalName(username) != null) {
            for (int i = 1;; i++) {
                String check = username + "-" + i;
                if (userRepository.findByCanonicalName(check) == null) {
                    username = check;
                    break;
                }
            }
        }

        return username;
    }

    private String normalizeString(String in) {
        return Normalizer.normalize(in, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    private String usernameFromRealname(UserProfile userProfile) {
        String username = normalizeString(userProfile.getFirstName() + "-" + userProfile.getLastName());
        if (username.length() > 40) {
            username = username.substring(0, 40);
        }
        return username;
     }
}
