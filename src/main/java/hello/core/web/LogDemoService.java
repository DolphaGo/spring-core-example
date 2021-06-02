package hello.core.web;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import hello.core.common.MyLogger;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class LogDemoService {

    private final ObjectProvider<MyLogger> myLoggerProvider;

    public void logic(final String id) {
        final MyLogger myLogger = myLoggerProvider.getObject();
        myLogger.log("service id = " + id);
    }
}
