package com.odinlascience.backend.modules.catalog.service;

import com.odinlascience.backend.modules.catalog.model.AppModule;
import com.odinlascience.backend.modules.catalog.model.UserModuleAccess;
import com.odinlascience.backend.modules.catalog.enums.ModuleType;
import com.odinlascience.backend.modules.catalog.repository.AppModuleRepository;
import com.odinlascience.backend.modules.catalog.repository.UserModuleAccessRepository;
import com.odinlascience.backend.user.model.User;
import com.odinlascience.backend.user.enums.RoleType;
import com.odinlascience.backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ModuleAccessServiceTest {

    @Autowired
    private ModuleAccessService moduleAccessService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AppModuleRepository moduleRepository;

    @Autowired
    private UserModuleAccessRepository accessRepository;

    private User adminUser;
    private User studentUser;
    private AppModule freeModule;
    private AppModule paidModule;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();

        adminUser = User.builder()
                .email("admin-access@example.com")
                .password("password")
                .firstName("Admin")
                .lastName("Access")
                .role(RoleType.ADMIN)
                .build();
        adminUser = userRepository.save(adminUser);

        studentUser = User.builder()
                .email("student-access@example.com")
                .password("password")
                .firstName("Student")
                .lastName("Access")
                .role(RoleType.STUDENT)
                .build();
        studentUser = userRepository.save(studentUser);

        freeModule = AppModule.builder()
                .moduleKey("TEST_FREE")
                .type(ModuleType.MUNIN_ATLAS)
                .price(0.0)
                .active(true)
                .build();
        freeModule = moduleRepository.save(freeModule);

        paidModule = AppModule.builder()
                .moduleKey("TEST_PAID")
                .type(ModuleType.HUGIN_LAB)
                .price(29.99)
                .active(true)
                .build();
        paidModule = moduleRepository.save(paidModule);
    }

    private void authenticateAs(User user) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(), null, Collections.emptyList());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void isModuleLocked_WhenNotAuthenticated_ReturnsTrue() {
        assertThat(moduleAccessService.isModuleLocked(paidModule)).isTrue();
    }

    @Test
    void isModuleLocked_ForAdmin_ReturnsFalse() {
        authenticateAs(adminUser);
        assertThat(moduleAccessService.isModuleLocked(paidModule)).isFalse();
    }

    @Test
    void isModuleLocked_ForFreeModule_ReturnsFalse() {
        authenticateAs(studentUser);
        assertThat(moduleAccessService.isModuleLocked(freeModule)).isFalse();
    }

    @Test
    void isModuleLocked_ForPaidModuleWithoutAccess_ReturnsTrue() {
        authenticateAs(studentUser);
        assertThat(moduleAccessService.isModuleLocked(paidModule)).isTrue();
    }

    @Test
    void isModuleLocked_ForPaidModuleWithAccess_ReturnsFalse() {
        authenticateAs(studentUser);
        moduleAccessService.grantPermanentAccess(studentUser, paidModule);
        assertThat(moduleAccessService.isModuleLocked(paidModule)).isFalse();
    }

    @Test
    void isModuleLocked_ForModuleWithNullPrice_ReturnsFalse() {
        authenticateAs(studentUser);
        AppModule nullPriceModule = AppModule.builder()
                .moduleKey("TEST_NULL")
                .type(ModuleType.MUNIN_ATLAS)
                .price(null)
                .active(true)
                .build();
        nullPriceModule = moduleRepository.save(nullPriceModule);
        assertThat(moduleAccessService.isModuleLocked(nullPriceModule)).isFalse();
    }

    @Test
    void hasAccess_WhenNotAuthenticated_ReturnsFalse() {
        assertThat(moduleAccessService.hasAccess("TEST_PAID")).isFalse();
    }

    @Test
    void hasAccess_ForAdmin_ReturnsTrue() {
        authenticateAs(adminUser);
        assertThat(moduleAccessService.hasAccess("TEST_PAID")).isTrue();
    }

    @Test
    void hasAccess_ForUserWithoutAccess_ReturnsFalse() {
        authenticateAs(studentUser);
        assertThat(moduleAccessService.hasAccess("TEST_PAID")).isFalse();
    }

    @Test
    void hasAccess_ForUserWithAccess_ReturnsTrue() {
        authenticateAs(studentUser);
        moduleAccessService.grantPermanentAccess(studentUser, paidModule);
        assertThat(moduleAccessService.hasAccess("TEST_PAID")).isTrue();
    }

    @Test
    void grantAccess_CreatesNewAccess() {
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);
        UserModuleAccess access = moduleAccessService.grantAccess(studentUser, paidModule, expiresAt);

        assertThat(access.getId()).isNotNull();
        assertThat(access.getUser().getId()).isEqualTo(studentUser.getId());
        assertThat(access.getModule().getId()).isEqualTo(paidModule.getId());
        assertThat(access.getHasAccess()).isTrue();
        assertThat(access.getExpiresAt()).isEqualTo(expiresAt);
    }

    @Test
    void grantAccess_UpdatesExistingAccess() {
        moduleAccessService.grantPermanentAccess(studentUser, paidModule);
        
        LocalDateTime newExpiry = LocalDateTime.now().plusDays(60);
        UserModuleAccess updated = moduleAccessService.grantAccess(studentUser, paidModule, newExpiry);

        assertThat(updated.getExpiresAt()).isEqualTo(newExpiry);
    }

    @Test
    void grantPermanentAccess_CreatesAccessWithNullExpiry() {
        UserModuleAccess access = moduleAccessService.grantPermanentAccess(studentUser, paidModule);

        assertThat(access.getExpiresAt()).isNull();
        assertThat(access.getHasAccess()).isTrue();
    }

    @Test
    void revokeAccess_SetsHasAccessToFalse() {
        moduleAccessService.grantPermanentAccess(studentUser, paidModule);
        moduleAccessService.revokeAccess(studentUser, paidModule);

        var accessOpt = accessRepository.findByUserIdAndModuleModuleKey(studentUser.getId(), paidModule.getModuleKey());
        assertThat(accessOpt).isPresent();
        assertThat(accessOpt.get().getHasAccess()).isFalse();
    }

    @Test
    void revokeAccess_WhenNoAccess_DoesNothing() {
        moduleAccessService.revokeAccess(studentUser, paidModule);
    }

    @Test
    void getCurrentUserAccessibleModules_WhenNotAuthenticated_ReturnsEmptyList() {
        List<UserModuleAccess> modules = moduleAccessService.getCurrentUserAccessibleModules();
        assertThat(modules).isEmpty();
    }

    @Test
    void getCurrentUserAccessibleModules_ReturnsUserModules() {
        authenticateAs(studentUser);
        moduleAccessService.grantPermanentAccess(studentUser, paidModule);

        List<UserModuleAccess> modules = moduleAccessService.getCurrentUserAccessibleModules();
        assertThat(modules).hasSize(1);
        assertThat(modules.get(0).getModule().getModuleKey()).isEqualTo("TEST_PAID");
    }
}
