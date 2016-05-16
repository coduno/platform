package uno.cod.platform.server.rest.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uno.cod.platform.server.core.dto.user.UserCreateDto;
import uno.cod.platform.server.core.repository.UserRepository;
import uno.cod.platform.server.rest.RestUrls;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest extends AbstractControllerTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void createUser() throws Exception {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setNick("testnick");
        userCreateDto.setEmail("test@test.at");
        userCreateDto.setPassword("testitest");

        mockMvc.perform(post(RestUrls.USERS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(userCreateDto))
        ).andExpect(status().isCreated());

        assertNotNull(userRepository.findByUsernameValue("testnick"));
    }
}