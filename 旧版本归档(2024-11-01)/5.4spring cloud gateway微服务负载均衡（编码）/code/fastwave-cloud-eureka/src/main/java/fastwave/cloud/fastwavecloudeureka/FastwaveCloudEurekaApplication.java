package fastwave.cloud.fastwavecloudeureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class FastwaveCloudEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(FastwaveCloudEurekaApplication.class, args);
    }

}
