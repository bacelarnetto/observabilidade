package br.com.ms.custom.loggers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Service
public class LoggingServiceImpl implements LoggingService{

    static Logger logger = LoggerFactory.getLogger(LoggingServiceImpl.class);

    @Override
    public void displayReq(HttpServletRequest request, Object body) {
        StringBuilder reqMessage = new StringBuilder();
        Map<String,String> parameters = getParameters(request);
        reqMessage.append("REQUEST ");
        reqMessage.append("method = [").append(request.getMethod()).append("] ");
        reqMessage.append("path = [").append(request.getRequestURI()).append("] ");
        if(!parameters.isEmpty()) {
            reqMessage.append("parameters = [").append(parameters).append("] ");
        }
        if(!Objects.isNull(body)) {
            reqMessage.append("body = [").append(body).append("]");
        }
        getMDC(request);
        logger.info("{}", reqMessage);
    }

    @Override
    public void displayResp(HttpServletRequest request, HttpServletResponse response, Object body) {
        StringBuilder respMessage = new StringBuilder();
        Map<String,String> headers = getHeaders(response);
        respMessage.append("RESPONSE ");
        respMessage.append("method = [").append(request.getMethod()).append("] ");
        respMessage.append("path = [").append(request.getRequestURI()).append("] ");
        if(!headers.isEmpty()) {
            respMessage.append("ResponseHeaders = [").append(headers).append("] ");
        }
        respMessage.append("responseBody = [").append(body).append("] ");
        getMDC(request);
        logger.info("{}",respMessage);
    }

    private Map<String,String> getHeaders(HttpServletResponse response) {
        Map<String,String> headers = new HashMap<>();
        Collection<String> headerMap = response.getHeaderNames();
        for(String str : headerMap) {
            headers.put(str,response.getHeader(str));
        }
        return headers;
    }

    private Map<String,String> getParameters(HttpServletRequest request) {
        Map<String,String> parameters = new HashMap<>();
        Enumeration<String> params = request.getParameterNames();
        while(params.hasMoreElements()) {
            String paramName = params.nextElement();
            String paramValue = request.getParameter(paramName);
            parameters.put(paramName,paramValue);
        }
        return parameters;
    }

    private void getMDC(HttpServletRequest request){
        MDC.clear();
        MDC.put("nameApp", getApplicationInfo().getName());
        MDC.put("versaoApp", getApplicationInfo().getVersion());
        MDC.put("groupIdApp", getApplicationInfo().getGroupId());
        MDC.put("ip", getIpClient(request));
        MDC.put("verb", request.getMethod());
        MDC.put("path", request.getRequestURI());
        logger.info("MDC registered");
    }


    public static String getIpClient(HttpServletRequest request){
        if(request != null){
            return request.getRemoteAddr()+":"+request.getRemotePort()+":"+request.getProtocol();
        }
        return "não é um app";
    }

    private static ApplicationInfo getApplicationInfo(){
        Properties properties = new Properties();
        ApplicationInfo applicationInfo = null;
        try {
            properties.load(LoggingServiceImpl.class.getResourceAsStream("/project.properties"));
            applicationInfo  = new ApplicationInfo(properties.getProperty("application.name"),properties.getProperty("application.version"),properties.getProperty("application.groupId"));
        } catch (IOException e) {
            logger.error("Erro ao encontrar o arquivo project.properties");
            applicationInfo  = new ApplicationInfo("Name indefinido","Version indefinido","GroupId indefinido");

        }
        return applicationInfo;
    }




}