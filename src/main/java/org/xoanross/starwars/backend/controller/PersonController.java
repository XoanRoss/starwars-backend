package org.xoanross.starwars.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xoanross.starwars.backend.dto.FilterDto;
import org.xoanross.starwars.backend.dto.PersonDto;
import org.xoanross.starwars.backend.service.PersonQueryService;

@Tag(name = "People", description = "Operations to query people from the Star Wars universe")
@RestController
@RequiredArgsConstructor
public class PersonController {

    private final PersonQueryService personQueryService;

    @Operation(
            summary = "Query paginated, sorted and filtered people",
            description = "Returns a page of people. The page size is configured with settings.page-size in application.properties."
    )
    @PostMapping("/people")
    public ResponseEntity<Page<PersonDto>> getPage(@RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(required = false, defaultValue = "id") String sortBy,
                                                   @RequestParam(required = false, defaultValue = "asc") String sortDir,
                                                   @RequestBody(required = false) FilterDto filterDto) {
        return ResponseEntity.ok(personQueryService.getPage(page, sortBy, sortDir, filterDto));
    }
}
