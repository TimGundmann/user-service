package dk.gundmann.users;

import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("!Test")

@Configuration
public class DiscoveryClientConfig {

}
