package org.xoanross.starwars.backend.config;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.xoanross.starwars.backend.client.swapi.SwapiClient;
import org.xoanross.starwars.backend.exception.model.ClientException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
class WebClientConfigIntegrationTest {

    @Autowired
    private SwapiClient swapiClient;

    @BeforeEach
    void reset() {
        WireMock.reset();
    }

    @Test
    @DisplayName("When remote returns 500 Then maps to ClientException with body snippet")
    void whenRemoteError_thenClientException() {
        stubFor(get(urlMatching("/people.*")).willReturn(aResponse()
                .withStatus(500)
                .withHeader("Content-Type", "application/json")
                .withBody("{\"error\":\"error\"}")));

        assertThatThrownBy(() -> swapiClient.getPeople())
                .isInstanceOf(ClientException.class)
                .hasMessageContaining("500")
                .hasMessageContaining("error");
    }
}

