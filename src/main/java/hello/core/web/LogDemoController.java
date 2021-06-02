package hello.core.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import hello.core.common.MyLogger;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LogDemoController {
    private final LogDemoService logDemoService;
    private final MyLogger myLogger;

    @RequestMapping("log-demo")
    @ResponseBody // 화면이 없어서 뷰 렌더링 거치지 않고 바로 문자로 반환할 것
    public String logDemo(HttpServletRequest request) throws InterruptedException { // 즉, Request가 들어온 시점에 제공받으면 되기 때문이다.
        final String requestURL = request.getRequestURL().toString();
        System.out.println("myLogger.getClass() = " + myLogger.getClass());
        myLogger.setRequestURL(requestURL);

        myLogger.log("controller test");
        Thread.sleep(10000); //요청마다 로거를 할당해주는 것을 확인할 수 있다.
        logDemoService.logic("testId");
        return "OK";
    }
}
