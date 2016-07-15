package uno.cod.platform.server.rest.controller;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uno.cod.platform.server.core.dto.user.ActivationTokenCreateDto;
import uno.cod.platform.server.core.repository.ActivationTokenRepository;
import uno.cod.platform.server.rest.RestUrls;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ActivationTokenControllerTest extends AbstractControllerTest {
    @Autowired
    private ActivationTokenRepository repository;

    @Test
    public void createUser() throws Exception {
        ActivationTokenCreateDto activationTokenCreateDto = new ActivationTokenCreateDto();
        activationTokenCreateDto.setNick("testnick");
        activationTokenCreateDto.setEmail("test@test.at");
        activationTokenCreateDto.setPassword("testitest");

        mockMvc.perform(post(RestUrls.ACTIVATION_TOKENS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(activationTokenCreateDto))
        ).andExpect(status().isCreated());

        assertNotNull(repository.findByEmail("test@test.at"));
    }
}