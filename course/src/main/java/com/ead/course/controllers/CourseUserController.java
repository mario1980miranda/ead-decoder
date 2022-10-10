package com.ead.course.controllers;

import com.ead.course.dtos.SubscriptionDto;
import com.ead.course.enums.UserStatus;
import com.ead.course.models.CourseModel;
import com.ead.course.models.UserModel;
import com.ead.course.services.CourseService;
import com.ead.course.services.UserService;
import com.ead.course.specifications.SpecificationTemplate;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    UserService userService;

    @PreAuthorize("hasAnyRole('INSTRUCTOR')")
    @GetMapping("/courses/{courseId}/users")
    public ResponseEntity<Object> getAllUsersByCourse(
            SpecificationTemplate.UserSpec spec,
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

        return ResponseEntity.status(HttpStatus.OK).body(userService.findAll(SpecificationTemplate.userCourseId(courseId).and(spec), pageable));
    }

    @PreAuthorize("hasAnyRole('STUDENT')")
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

        if (courseService.existsByCourseAndUser(courseId, subscriptionDto.getUserId())) {
            log.warn("Subscription already exists (course {}, user {})", courseId, subscriptionDto.getUserId());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Subscription already exists");
        }

        Optional<UserModel> userModelOptional = userService.findById(subscriptionDto.getUserId());

        if (!userModelOptional.isPresent()) {
            log.warn("User {} not found.", subscriptionDto.getUserId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        if (userModelOptional.get().getUserStatus().equals(UserStatus.BLOCKED.toString())) {
            log.warn("User {} is blocked", subscriptionDto.getUserId());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Subscription already exists");
        }

        courseService.saveSubscriptionUserInCourseAndSendNotification(courseModelOptional.get(), userModelOptional.get());

        log.debug("Subscription created successfully");

        return ResponseEntity.status(HttpStatus.CREATED).body("Subscription created successfully");
    }

}
