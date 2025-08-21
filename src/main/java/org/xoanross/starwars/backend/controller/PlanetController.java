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
import org.xoanross.starwars.backend.dto.PlanetDto;
import org.xoanross.starwars.backend.service.PlanetQueryService;

@Tag(name = "Planets", description = "Operations to query planets from the Star Wars universe")
@RestController
@RequiredArgsConstructor
public class PlanetController {

    private final PlanetQueryService planetsService;

    @Operation(
            summary = "Query paginated, sorted and filtered planets",
            description = "Returns a page of planets. The page size is configured with settings.page-size in application.properties."
    )
    @PostMapping("/planets")
    public ResponseEntity<Page<PlanetDto>> getPage(@RequestParam(defaultValue = "1") int page,
                                                   @RequestParam(required = false, defaultValue = "id") String sortBy,
                                                   @RequestParam(required = false, defaultValue = "asc") String sortDir,
                                                   @RequestBody(required = false) FilterDto filterDto) {
        return ResponseEntity.ok(planetsService.getPage(page, sortBy, sortDir, filterDto));
    }
}
