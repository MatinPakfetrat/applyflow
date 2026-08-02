package com.matin.applyflow.service;

import com.matin.applyflow.dto.ApplicationRequest;
import com.matin.applyflow.dto.ApplicationResponse;
import com.matin.applyflow.exception.ResourceNotFoundException;
import com.matin.applyflow.model.Application;
import com.matin.applyflow.model.ApplicationStatus;
import com.matin.applyflow.model.User;
import com.matin.applyflow.repository.ApplicationRepository;
import com.matin.applyflow.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import static org.assertj.core.api.Assertions.assertThat;


import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ApplicationService applicationService;

    private User currentUser;

    @BeforeEach
    void setUp(){
        currentUser = new User();
        ReflectionTestUtils.setField(currentUser, "id", 1L);
        currentUser.setUsername("matin");

        // Fake an authenticated security context, the same way JwtFilter would populate it
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("matin");

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("matin")).thenReturn(Optional.of(currentUser));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext(); // avoid leaking state into other tests
    }

    @Test
    void createApplication_savesAndReturnsResponse(){
        ApplicationRequest request = new ApplicationRequest();
        request.setCompanyName("Google");
        request.setJobTitle("Backend Engineer");
        request.setSalary(new BigDecimal("130000"));
        request.setStatus(ApplicationStatus.APPLIED);

        Application savedEntity = new Application();
        ReflectionTestUtils.setField(savedEntity, "id", 10L);
        savedEntity.setCompanyName("Google");
        savedEntity.setJobTitle("Backend Engineer");
        savedEntity.setSalary(new BigDecimal("130000"));
        savedEntity.setStatus(ApplicationStatus.APPLIED);
        savedEntity.setUser(currentUser);

        when(applicationRepository.save(any(Application.class))).thenReturn(savedEntity);

        ApplicationResponse response = applicationService.createApplication(request);

        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getCompanyName()).isEqualTo("Google");
        verify(applicationRepository).save(any(Application.class));
    }

    @Test
    void updateApplication_whenNotOwner_throwsResourceNotFound(){
        User someoneElse = new User();
        ReflectionTestUtils.setField(someoneElse, "id", 99L);

        Application existing = new Application();
        ReflectionTestUtils.setField(existing, "id", 5L);
        existing.setUser(someoneElse);

        when(applicationRepository.findById(5L)).thenReturn(Optional.of(existing));

        ApplicationRequest request = new ApplicationRequest();
        request.setCompanyName("Meta");
        request.setJobTitle("SWE");
        request.setSalary(BigDecimal.TEN);

        assertThatThrownBy(() -> applicationService.updateApplication(5L, request)).
                isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteApplicationById_whenNotFound_throwsResourceNotFound(){
        when(applicationRepository.findById(100L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> applicationService.deleteApplicationById(100L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
