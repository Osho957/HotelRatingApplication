package com.osho.userservice.config.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import reactor.util.annotation.NonNullApi;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Objects;


public class RestTemplateInterceptor implements ClientHttpRequestInterceptor {

    private final OAuth2AuthorizedClientManager manager;

    private final Logger logger= LoggerFactory.getLogger(RestTemplateInterceptor.class);

    public RestTemplateInterceptor(OAuth2AuthorizedClientManager manager) {
        this.manager = manager;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String token = Objects.requireNonNull(manager.authorize(OAuth2AuthorizeRequest.withClientRegistrationId("my-internal-client").principal("internal").build())).getAccessToken().getTokenValue();

        logger.info("Rest Template interceptor: Token :  {} ",token);

        request.getHeaders().add("Authorization","Bearer "+token);
        return execution.execute(request,body);
    }
}