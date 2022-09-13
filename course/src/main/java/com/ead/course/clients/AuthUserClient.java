package com.ead.course.clients;

import com.ead.course.dtos.ResponsePageDto;
import com.ead.course.dtos.UserDto;
import com.ead.course.services.UtilsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

@Log4j2
@Component
public class AuthUserClient {

    @Autowired
    UtilsService utilsService;
    @Autowired
    RestTemplate restTemplate;

    @Value("${ead.api.url.authuser}")
    String REQUEST_URL_AUTHUSER;

    public Page<UserDto> getAllUsersByCourse(UUID courseId, Pageable pageable) {

        ResponseEntity<ResponsePageDto<UserDto>> result = null;
        List<UserDto> searchResult = null;

        String url = REQUEST_URL_AUTHUSER + utilsService.createUrlGetAllUsersByCourse(courseId, pageable);

        log.debug("Request URL {}", url);
        log.info("Request URL {}", url);

        try {

            ParameterizedTypeReference<ResponsePageDto<UserDto>> responseType =
                    new ParameterizedTypeReference<ResponsePageDto<UserDto>>() {};
            result = restTemplate.exchange(url, HttpMethod.GET, null, responseType);

            searchResult = result.getBody().getContent();

            log.debug("Response number of elements: {}", searchResult.size());

        } catch (HttpStatusCodeException e) {

            log.error("Error request /users {}", e);
        }

        log.info("Ending request /users courseId {}", courseId);

        return result.getBody();
    }

    public ResponseEntity<UserDto> getOneUserById(UUID userId) {

        String url = REQUEST_URL_AUTHUSER + "/users/" + userId;

        return restTemplate.exchange(url, HttpMethod.GET, null, UserDto.class);
    }

}