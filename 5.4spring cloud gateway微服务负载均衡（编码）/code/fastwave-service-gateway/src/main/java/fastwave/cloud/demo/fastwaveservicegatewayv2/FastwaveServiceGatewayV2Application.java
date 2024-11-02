package fastwave.cloud.demo.fastwaveservicegatewayv2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FastwaveServiceGatewayV2Application {

    public static void main(String[] args) {
        SpringApplication.run(FastwaveServiceGatewayV2Application.class, args);
    }

}
