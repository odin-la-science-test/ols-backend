package com.odinlascience.backend;

import com.odinlascience.backend.security.TestSecurityConfig;
import com.odinlascience.backend.security.TestSecuritySupport;
import com.odinlascience.backend.security.service.JwtService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
public abstract class AbstractControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected JwtService jwtService;

    protected RequestPostProcessor auth() {
        return TestSecuritySupport.authHeader(jwtService);
    }
}
