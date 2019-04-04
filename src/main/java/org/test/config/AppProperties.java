package org.test.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    public String url;
    public String username;
    public String password;

}
