package com.ead.course.controllers;

import com.ead.course.clients.AuthUserClient;
import com.ead.course.dtos.SubscriptionDto;
import com.ead.course.dtos.UserDto;
import com.ead.course.enums.UserStatus;
import com.ead.course.models.CourseModel;
import com.ead.course.models.CourseUserModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.CourseUserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Optional;
import java.util.UUID;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseUserController {

    @Autowired
    AuthUserClient authUserClient;
    @Autowired
    CourseService courseService;
    @Autowired
    CourseUserService courseUserService;

    @GetMapping("/courses/{courseId}/users")
    public ResponseEntity<Object> getAllUsersByCourse(
            @PageableDefault(page = 0, size = 10, sort = "userId", direction = Sort.Direction.ASC)
            Pageable pageable,
            @PathVariable(value = "courseId")
            UUID courseId
    ) {

        log.debug("GET getAllUsersByCourse courseId {}", courseId);

        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);

        if (!courseModelOptional.isPresent()) {
            log.warn("Course not found {}", courseId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found.");
        }

        return ResponseEntity.status(HttpStatus.OK).body(authUserClient.getAllUsersByCourse(courseId, pageable));
    }

    @PostMapping("/courses/{courseId}/users/subscription")
    public ResponseEntity<Object> saveSubscriptionUserInCourse(
            @PathVariable(value = "courseId")
            UUID courseId,
            @Validated
            @RequestBody
            SubscriptionDto subscriptionDto
    ) {

        log.debug("POST saveSubscriptionUserInCourse courseId {}", courseId);

        ResponseEntity<UserDto> responseUser = null;
        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);

        if (!courseModelOptional.isPresent()) {
            log.warn("Course not found courseId", courseId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found.");
        }

        if (courseUserService.existsByCourseAndUserId(courseModelOptional.get(), subscriptionDto.getUserId())) {
            log.warn("Error: subscription already exists!", courseModelOptional.get().getCourseId());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: subscription already exists!");
        }

        try {

            responseUser = authUserClient.getOneUserById(subscriptionDto.getUserId());

            if (responseUser.getBody().getUserStatus().equals(UserStatus.BLOCKED)) {
                log.warn("User is blocked {}.", subscriptionDto.getUserId());
                return ResponseEntity.status(HttpStatus.CONFLICT).body("User is blocked.");
            }

        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                log.warn("User not found {}", subscriptionDto.getUserId());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
        }

        // CourseUserModel courseUserModel = courseUserService.save(courseModelOptional.get().convertToCourseUserModel(subscriptionDto.getUserId()));
        CourseUserModel courseUserModel = courseUserService.saveAndSendSubscriptionUserInCourse(courseModelOptional.get().convertToCourseUserModel(subscriptionDto.getUserId()));

        log.debug("CourseUser created successfully courseUser->Id", courseUserModel.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(courseUserModel);
    }

    @DeleteMapping("/courses/users/{userId}")
    public ResponseEntity<Object> deleteCourseUserByUser(
            @PathVariable(value = "userId")
            UUID userId
    ) {

        log.debug("DELETE deleteCourseUserByUser userId {}", userId);

        if (!courseUserService.existsByUserId(userId)) {
            log.warn("CourseUser not found by user {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("CourseUser not found.");
        }

        courseUserService.deleteCourseUserByUser(userId);

        log.info("CourseUser deleted successfully.");

        return ResponseEntity.status(HttpStatus.OK).body("CourseUser deleted successfully.");
    }
}
