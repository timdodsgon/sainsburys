package services;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import org.mockito.ArgumentMatcher;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public abstract class BaseScaperServiceTest {

    @SuppressWarnings("unchecked")
    protected void assertThatLoggerMessageIs(Appender appender, int invocations, String message) {
        verify(appender, times(invocations)).doAppend(argThat(new ArgumentMatcher() {
            @Override
            public boolean matches(Object argument) {
                return ((ILoggingEvent) argument).getFormattedMessage().equals(message);
            }
        }));
    }
}
