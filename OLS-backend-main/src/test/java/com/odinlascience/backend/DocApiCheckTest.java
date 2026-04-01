package com.odinlascience.backend;

import io.swagger.v3.oas.annotations.Operation;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Map;

@SpringBootTest 
class DocApiCheckTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void checkAnnotationsDirectly() {
        System.out.println("--- 🕵️ Audit Statique des Annotations ---");
        
        Map<String, Object> controllers = context.getBeansWithAnnotation(RestController.class);
        
        StringBuilder errors = new StringBuilder();
        int testedMethods = 0;
        int validMethods = 0;

        for (Object controllerBean : controllers.values()) {
            Class<?> realClass = AopUtils.getTargetClass(controllerBean);
            
            if (!realClass.getName().startsWith("com.odinlascience")) {
                continue;
            }

            System.out.println("🔎 Scan de la classe : " + realClass.getSimpleName());

            for (Method method : realClass.getDeclaredMethods()) {
                boolean isEndpoint = java.util.Arrays.stream(method.getAnnotations())
                        .anyMatch(a -> a.annotationType().getSimpleName().endsWith("Mapping"));

                if (isEndpoint) {
                    testedMethods++;
                    
                    Operation apiOp = method.getAnnotation(Operation.class);

                    if (apiOp == null) {
                        errors.append(String.format("\n❌ %s.%s() : Aucune annotation @Operation", 
                                realClass.getSimpleName(), method.getName()));
                        continue;
                    }

                    boolean hasSummary = StringUtils.hasText(apiOp.summary());
                    boolean hasDesc = StringUtils.hasText(apiOp.description());

                    if (hasSummary && hasDesc) {
                        validMethods++;
                    } else {
                        errors.append(String.format("\n⚠️ %s.%s() :", realClass.getSimpleName(), method.getName()));
                        if (!hasSummary) errors.append(" [Manque Summary]");
                        if (!hasDesc) errors.append(" [Manque Description]");
                    }
                }
            }
        }

        System.out.println("------------------------------------------------");
        System.out.printf("📊 Résultat : %d/%d méthodes bien documentées.%n", validMethods, testedMethods);
        
        if (errors.length() > 0) {
            throw new AssertionError("Documentation incomplète détectée :" + errors.toString());
        }
    }
}