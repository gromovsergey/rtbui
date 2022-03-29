package app.programmatic.ui.common.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.web.util.DefaultUriTemplateHandler;

@Configuration
public class OAuthConfig {

    @Bean
    @ConfigurationProperties(prefix = "security.oauth2.client")
    public ClientCredentialsResourceDetails clientCredentialsResourceDetails() {
        return new ClientCredentialsResourceDetails();
    }

    @Bean
    @Qualifier("paypalRestTemplate")
    public OAuth2RestTemplate paypalRestTemplate(@Value("${paypal.api.url}") String paypalBaseUrl) {
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(clientCredentialsResourceDetails(), new DefaultOAuth2ClientContext());
        DefaultUriTemplateHandler uriTemplateHandler = new DefaultUriTemplateHandler();
        uriTemplateHandler.setBaseUrl(paypalBaseUrl);
        restTemplate.setUriTemplateHandler(uriTemplateHandler);
        return restTemplate;
    }
}
