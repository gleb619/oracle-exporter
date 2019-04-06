package org.test.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.test.config.Constants.DEFAULT_PORT;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@ConfigurationProperties(prefix = "app")
public class AppProperties implements Serializable {

    public String url;
    public String username;
    public String password;

    private String host;
    private String port;
    private String sid;
    private String serviceName;

    public AppProperties merge(AppProperties appProperties) {
        AppPropertiesBuilder builder = assemble().toBuilder();

        if (isBlank(builder.url)) {
            builder.url(appProperties.url);
        }
        if (isBlank(builder.username)) {
            builder.url(appProperties.username);
        }
        if (isBlank(builder.password)) {
            builder.url(appProperties.password);
        }

        return builder
                .build();
    }

    private AppProperties assemble() {
        if (isBlank(port)) {
            setPort(DEFAULT_PORT);
        }

        if (isBlank(url) && isNotBlank(host)) {
            if (isNotBlank(sid)) {
                setUrl(String.format("jdbc:oracle:thin:@%s:%s:%s", host, port, sid));
                setSid(null);
            } else if (isNotBlank(serviceName)) {
                setUrl(String.format("jdbc:oracle:thin:@//%s:%s/%s", host, port, serviceName));
                setServiceName(null);
            }

            setHost(null);
        }

        setPort(null);

        return this;
    }

}
