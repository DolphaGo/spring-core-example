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
    private final MyLogger myLogger; // 그냥 실행하면 현재 request 스콥인 상태에서는 MyLogger가 스콥이 아니라서(생존 범위가 아님) 스프링에서 DI를 원하지만 줄 것이 없음
    // -> Provider로 해결

    @RequestMapping("log-demo")
    @ResponseBody // 화면이 없어서 뷰 렌더링 거치지 않고 바로 문자로 반환할 것
    public String logDemo(HttpServletRequest request) {
        final String requestURL = request.getRequestURL().toString();
        myLogger.setRequestURL(requestURL);

        myLogger.log("controller test");
        logDemoService.logic("testId");
        return "OK";
    }
}
