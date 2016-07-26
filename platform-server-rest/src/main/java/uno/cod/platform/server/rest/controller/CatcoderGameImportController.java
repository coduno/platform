package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uno.cod.platform.server.codingcontest.sync.service.CatcoderGameImportService;
import uno.cod.platform.server.core.security.AllowedForAdmin;
import uno.cod.platform.server.rest.RestUrls;

import java.io.IOException;
import java.util.UUID;

@RestController
public class CatcoderGameImportController {
    private CatcoderGameImportService catcoderGameImportService;

    @Autowired
    public CatcoderGameImportController(CatcoderGameImportService catcoderGameImportService) {
        this.catcoderGameImportService = catcoderGameImportService;
    }

    @AllowedForAdmin
    @RequestMapping(value = RestUrls.CATCODER_GAME_UPLOAD, method = RequestMethod.POST)
    public ResponseEntity<String> gameUploadZip(@RequestParam("file") MultipartFile file,
                                                @RequestParam("organization") UUID organization) throws IOException {
        return new ResponseEntity<>(catcoderGameImportService.createChallengeTemplateFromGameResources(file, organization), HttpStatus.OK);
    }
}
