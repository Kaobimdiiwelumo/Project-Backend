package Project.FinalYear.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;

@Component
public class EndpointPrinter {

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Value("${app.print-endpoints:false}")
    private boolean printEndpoints;

    @Bean
    public CommandLineRunner printEndpoints() {
        return args -> {
            if (printEndpoints) {
                Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
                handlerMethods.forEach((requestMappingInfo, handlerMethod) -> {
                    System.out.println("Pattern: " + requestMappingInfo.getPatternsCondition().getPatterns());
                    System.out.println("Methods: " + requestMappingInfo.getMethodsCondition().getMethods());
                    System.out.println("Handler: " + handlerMethod);
                    System.out.println("--------------------------------------");
                });
            }
        };
    }
}