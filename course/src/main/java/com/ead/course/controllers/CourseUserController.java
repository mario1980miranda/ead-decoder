package com.ead.course.controllers;

import com.ead.course.dtos.SubscriptionDto;
import com.ead.course.models.CourseModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@Log4j2
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseUserController {

    @Autowired
    CourseService courseService;
    @Autowired
    UserService courseUserService;

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

        return ResponseEntity.status(HttpStatus.OK).body("STATE TRANSFER PATTERN");
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

        Optional<CourseModel> courseModelOptional = courseService.findById(courseId);

        if (!courseModelOptional.isPresent()) {
            log.warn("Course not found courseId", courseId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Course not found.");
        }
        // TODO: verificacoes state transfer
        log.debug("CourseUser created successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body("STATE TRANSFER PATTERN");
    }

}
