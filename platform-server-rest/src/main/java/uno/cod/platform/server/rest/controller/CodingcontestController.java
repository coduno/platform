package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uno.cod.platform.server.codingcontest.sync.dto.CodingcontestDto;
import uno.cod.platform.server.codingcontest.sync.dto.ContestInfoDto;
import uno.cod.platform.server.codingcontest.sync.dto.ParticipationDto;
import uno.cod.platform.server.codingcontest.sync.service.CodingcontestSyncService;
import uno.cod.platform.server.core.security.AllowedForAdmin;

import java.util.UUID;

@RestController
public class CodingcontestController {
    private final CodingcontestSyncService service;

    @Autowired
    public CodingcontestController(CodingcontestSyncService service) {
        this.service = service;
    }

    @AllowedForAdmin
    @RequestMapping(value = "/contestuploadraw", method = RequestMethod.POST)
    public ResponseEntity<String> contestuploadraw(@RequestBody CodingcontestDto dto) {
        service.createContest(dto);
        return new ResponseEntity<>("success", HttpStatus.OK);
    }

    @AllowedForAdmin
    @RequestMapping(value = "/useruploadraw", method = RequestMethod.POST)
    public void useruploadraw(@RequestBody ParticipationDto dto) {
        //TODO somehow reference the contest or at least the location
        service.updateUserFromCodingcontest(dto);
    }

    @AllowedForAdmin
    @RequestMapping(value = "/api/users/{uuid}/activate", method = RequestMethod.POST)
    public void activate(@PathVariable("uuid") UUID uuid) {
        /* dummy */
    }


    @AllowedForAdmin
    @RequestMapping(value = "/api/contests/{uuid}/report/json", method = RequestMethod.GET)
    public ContestInfoDto getResults(@PathVariable("uuid") UUID uuid) {
        return service.getResults(uuid);
    }
}