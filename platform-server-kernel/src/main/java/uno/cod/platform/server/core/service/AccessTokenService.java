package uno.cod.platform.server.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uno.cod.platform.server.core.domain.AccessToken;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.user.accesstoken.AccessTokenDto;
import uno.cod.platform.server.core.dto.user.accesstoken.CreatedAccessTokenDto;
import uno.cod.platform.server.core.exception.CodunoNoSuchElementException;
import uno.cod.platform.server.core.repository.AccessTokenRepository;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AccessTokenService {
    private AccessTokenRepository accessTokenRepository;
    private PasswordEncoder passwordEncoder;
    private SecureRandom random = new SecureRandom();

    @Autowired
    public AccessTokenService(AccessTokenRepository accessTokenRepository, PasswordEncoder passwordEncoder) {
        this.accessTokenRepository = accessTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Set<AccessTokenDto> listAllAccessTokensForUser(User user) {
        return accessTokenRepository.findByUser(user).stream().map(AccessTokenDto::new).collect(Collectors.toSet());
    }

    public CreatedAccessTokenDto createToken(String comment, User user) {
        String token = new BigInteger(130, random).toString(32);
        AccessToken accessToken = new AccessToken();
        accessToken.setUser(user);
        accessToken.setToken(passwordEncoder.encode(token));
        accessToken.setComment(comment);

        accessToken = accessTokenRepository.save(accessToken);

        byte[] bytes = (accessToken.getId() + ":" + token).getBytes();
        return new CreatedAccessTokenDto(new String(Base64.encode(bytes)));
    }

    public void deleteToken(UUID token, User user) {
        AccessToken accessToken = accessTokenRepository.getOne(token);
        if (accessToken == null) {
            throw new CodunoNoSuchElementException("token.invalid");
        }
        if (!accessToken.getUser().equals(user)) {
            throw new CodunoNoSuchElementException("token.invalid");
        }
        accessTokenRepository.delete(accessToken);
    }

    public UserDetails loadByAccessToken(UUID id, String token) {
        AccessToken accessToken = accessTokenRepository.findById(id);
        if (accessToken == null) {
            return null;
        }

        if (!passwordEncoder.matches(token, accessToken.getToken())) {
            return null;
        }

        accessToken.setLastUsed(ZonedDateTime.now());
        accessToken = accessTokenRepository.save(accessToken);
        return accessToken.getUser();
    }
}
