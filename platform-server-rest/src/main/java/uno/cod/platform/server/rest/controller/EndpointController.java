package uno.cod.platform.server.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uno.cod.platform.server.core.dto.endpoint.EndpointCreateDto;
import uno.cod.platform.server.core.dto.endpoint.EndpointShowDto;
import uno.cod.platform.server.core.security.AllowedForAdmin;
import uno.cod.platform.server.core.service.EndpointService;
import uno.cod.platform.server.rest.RestUrls;

import java.util.List;
import java.util.UUID;

@RestController
public class EndpointController {
    private final EndpointService service;

    @Autowired
    public EndpointController(EndpointService service) {
        this.service = service;
    }

    @RequestMapping(value = RestUrls.ENDPOINTS, method = RequestMethod.GET)
    @AllowedForAdmin
    public ResponseEntity<List<EndpointShowDto>> findAll() {
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }

    @RequestMapping(value = RestUrls.ENDPOINTS, method = RequestMethod.POST)
    @AllowedForAdmin
    public ResponseEntity<UUID> create(@RequestBody EndpointCreateDto dto) {
        return new ResponseEntity<>(service.createFromDto(dto), HttpStatus.CREATED);
    }


}
