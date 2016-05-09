package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import uno.cod.platform.server.core.domain.User;
import uno.cod.platform.server.core.dto.user.*;
import uno.cod.platform.server.core.service.UserService;
import uno.cod.platform.server.rest.RestUrls;

import javax.validation.Valid;
import java.util.List;

@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = RestUrls.USERS, method = RequestMethod.POST)
    public ResponseEntity<String> create(@Valid @RequestBody UserCreateDto dto) {
        userService.createFromDto(dto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

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
        return new ResponseEntity<>(new CurrentUserDto(user), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.USER, method = RequestMethod.PUT)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserShowDto> updateProfileDetails(@Valid @RequestBody UserUpdateProfileDetailsDto dto, @AuthenticationPrincipal User user) {
        return new ResponseEntity<>(userService.update(dto, user), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.USER_SEARCH, method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserShowDto>> searchUsers(@RequestParam String searchValue){
        return new ResponseEntity<>(userService.listUsersLikeNameOrEmail(searchValue), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.USER_PASSWORD, method = RequestMethod.PUT)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateMyPassword(@Valid @RequestBody UserPasswordChangeDto dto, @AuthenticationPrincipal User user) {
        userService.updatePassword(dto, user);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
