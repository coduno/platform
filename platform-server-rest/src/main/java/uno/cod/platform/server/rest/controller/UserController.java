package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.user.*;
import uno.cod.platform.server.core.service.PasswordResetService;
import uno.cod.platform.server.core.service.UserService;
import uno.cod.platform.server.rest.RestUrls;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.List;

@RestController
public class UserController {
    private final UserService userService;
    private final PasswordResetService passwordResetService;

    @Autowired
    public UserController(UserService userService,
                          PasswordResetService passwordResetService) {
        this.userService = userService;
        this.passwordResetService = passwordResetService;
    }

    @RequestMapping(value = RestUrls.USERS, method = RequestMethod.POST)
    public ResponseEntity<String> create(@Valid @RequestBody UserCreateDto dto) throws MessagingException {
        userService.createFromDto(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /*@RequestMapping(value = RestUrls.USERS, method = RequestMethod.GET)
    public ResponseEntity<String> create(@PathVariable("username") String username) throws MessagingException {

        // TODO: call correct method in service

        return new ResponseEntity<>(HttpStatus.CREATED);
    }*/

    @RequestMapping(value = RestUrls.USERS_USERNAME, method = RequestMethod.GET)
    public ResponseEntity<UserShowDto> querySingleUser(@PathVariable("username") String username) {
        return new ResponseEntity<>(userService.findByUsername(username), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.USERS, method = RequestMethod.GET)
    public List<UserShortShowDto> list() {
        return userService.listUsers();
    }

    @RequestMapping(value = RestUrls.USER, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CurrentUserDto> get(@AuthenticationPrincipal User user) {
        return new ResponseEntity<>(userService.findCurrentUser(user.getId()), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.USER, method = RequestMethod.PUT)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserShowDto> updateProfileDetails(@Valid @RequestBody UserUpdateProfileDetailsDto dto, @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(userService.update(dto, user), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.USER_SEARCH, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserShortShowDto>> searchUsers(@RequestParam String searchValue) {
        return new ResponseEntity<>(userService.listUsersByUsernameContaining(searchValue), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.USER_PASSWORD, method = RequestMethod.PUT)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateMyPassword(@Valid @RequestBody UserPasswordChangeDto dto, @AuthenticationPrincipal User user) {
        userService.updatePassword(dto, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.USER_PASSWORD_RESET, method = RequestMethod.POST)
    public ResponseEntity<String> sendResetEmail(@Valid @RequestBody UserPasswordResetMailDto dto) {
        passwordResetService.sendResetTokenMail(dto.getEmail());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.USER_PASSWORD_RESET, method = RequestMethod.PUT)
    public ResponseEntity<String> resetPassword(@Valid @RequestBody UserPasswordResetDto dto) {
        passwordResetService.resetPassword(dto.getToken(), dto.getPassword());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
