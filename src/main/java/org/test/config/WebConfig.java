package org.test.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.test.config.Constants.ID;
import static org.test.config.Constants.START_REQUEST;

@Slf4j
@Configuration
public class WebConfig {

    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter loggingFilter = new ExporterCommonsRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(false);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(true);
        loggingFilter.setIncludeHeaders(false);
        loggingFilter.setMaxPayloadLength(500);

        return loggingFilter;
    }

    public static class ExporterCommonsRequestLoggingFilter extends CommonsRequestLoggingFilter {

        private static String abbreviate(TimeUnit unit) {
            switch (unit) {
                case NANOSECONDS:
                    return "ns";
                case MICROSECONDS:
                    return "μs";
                case MILLISECONDS:
                    return "ms";
                case SECONDS:
                    return "s";
                case MINUTES:
                    return "min";
                case HOURS:
                    return "h";
                case DAYS:
                    return "d";
                default:
                    throw new AssertionError();
            }
        }

        @Override
        protected boolean shouldLog(HttpServletRequest request) {
            return Boolean.TRUE;
        }

        @Override
        protected void beforeRequest(HttpServletRequest request, String message) {
            request.setAttribute(START_REQUEST, System.nanoTime());
            request.setAttribute(ID, UUID.randomUUID().toString().substring(0, 7));
            log.info(String.format("[%s] %s", request.getAttribute(ID), message));
        }

        @Override
        protected void afterRequest(HttpServletRequest request, String message) {
            Long start = (Long) request.getAttribute(START_REQUEST);
            request.removeAttribute(START_REQUEST);
            String id = (String) request.getAttribute(ID);
            request.removeAttribute(ID);
            long nanos = System.nanoTime() - start;
            TimeUnit unit = chooseUnit(nanos);
            double value = (double) nanos / (double) TimeUnit.NANOSECONDS.convert(1L, unit);
            log.info(String.format("[%s] %s - elapsed %s %s", id, message, formatCompact4Digits(value), abbreviate(unit)));
        }

        private TimeUnit chooseUnit(long nanos) {
            if (TimeUnit.HOURS.convert(nanos, TimeUnit.NANOSECONDS) > 0L) {
                return TimeUnit.HOURS;
            } else if (TimeUnit.MINUTES.convert(nanos, TimeUnit.NANOSECONDS) > 0L) {
                return TimeUnit.MINUTES;
            } else if (TimeUnit.SECONDS.convert(nanos, TimeUnit.NANOSECONDS) > 0L) {
                return TimeUnit.SECONDS;
            } else if (TimeUnit.MILLISECONDS.convert(nanos, TimeUnit.NANOSECONDS) > 0L) {
                return TimeUnit.MILLISECONDS;
            } else {
                return TimeUnit.MICROSECONDS.convert(nanos, TimeUnit.NANOSECONDS) > 0L ? TimeUnit.MICROSECONDS : TimeUnit.NANOSECONDS;
            }
        }

        private String formatCompact4Digits(double value) {
            return String.format(Locale.ROOT, "%.4g", value);
        }

    }

}
