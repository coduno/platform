package uno.cod.platform.server.core.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import uno.cod.platform.server.core.domain.Language;
import uno.cod.platform.server.core.dto.language.LanguageShowDto;
import uno.cod.platform.server.core.repository.LanguageRepository;
import uno.cod.platform.server.core.service.util.LanguageTestUtil;

import java.util.Collections;
import java.util.List;

public class LanguageServiceTest {
    private LanguageService service;
    private LanguageRepository repository;

    @Before
    public void setUp() throws Exception {
        this.repository = Mockito.mock(LanguageRepository.class);
        service = new LanguageService(repository);
    }

    @Test
    public void findAll() throws Exception {
        Language language = LanguageTestUtil.getLanguage();
        Mockito.when(repository.findAll()).thenReturn(Collections.singletonList(language));

        List<LanguageShowDto> dtos = service.findAll();

        Assert.assertEquals(dtos.size(), 1);
        LanguageShowDto dto = dtos.get(0);
        Assert.assertEquals(dto.getId(), language.getId());
        Assert.assertEquals(dto.getName(), language.getName());
        Assert.assertEquals(dto.getCanonicalName(), language.getCanonicalName());
    }
}