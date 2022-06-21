package com.safetynet.safetynetalerts.logs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class RequestLogging extends OncePerRequestFilter {

    private final static String lineSeparator = System.lineSeparator();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // allows reading the body multiple times (avoids making body consumed when intercepted)
        ContentCachingRequestWrapper  contentCachingRequestWrapper  = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper contentCachingResponseWrapper = new ContentCachingResponseWrapper(response);

        filterChain.doFilter(contentCachingRequestWrapper, contentCachingResponseWrapper);

        String logMessage = lineSeparator + getRequestLogs(request, contentCachingRequestWrapper) +
                            lineSeparator + lineSeparator + getResponseLogs(response, contentCachingResponseWrapper);
        log.info(logMessage);

        contentCachingResponseWrapper.copyBodyToResponse();
    }

    private String getRequestLogs(HttpServletRequest request,
                                  ContentCachingRequestWrapper contentCachingRequestWrapper) {
        String requestBody = getStringValue(contentCachingRequestWrapper.getContentAsByteArray(),
                                            request.getCharacterEncoding());

        return "REQUEST:" + lineSeparator +
               "HTTP Method = " + request.getMethod() + lineSeparator +
               "Request URI = " + request.getRequestURI() + lineSeparator +
               "Parameters = " + getRequestParameters(request) + lineSeparator +
               "Headers = " + getRequestHeaders(request) + lineSeparator +
               "Body = " + requestBody;
    }

    private String getResponseLogs(HttpServletResponse response,
                                   ContentCachingResponseWrapper contentCachingResponseWrapper) {

        String requestBody = getStringValue(contentCachingResponseWrapper.getContentAsByteArray(),
                                            response.getCharacterEncoding());

        int responseCode = response.getStatus();

        final String responseMessage = "RESPONSE:" + lineSeparator +
                                       "Status = " + responseCode + lineSeparator +
                                       "Headers = " + getResponseHeaders(response) + lineSeparator +
                                       "Body = " + requestBody;

        if (responseCode >= 400) {
            log.error("An error occurred:" + lineSeparator + requestBody);
        }

        return responseMessage;
    }

    private Map<String, String> getRequestHeaders(HttpServletRequest request) {
        Map<String, String> headers     = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        return headers;
    }

    private Map<String, String> getResponseHeaders(HttpServletResponse response) {
        Map<String, String> headers     = new HashMap<>();
        Collection<String>  headerNames = response.getHeaderNames();
        for (String str : headerNames) {
            headers.put(str, response.getHeader(str));
        }
        return headers;
    }

    /**
     * Gets request parameters.
     *
     * @param request
     *         HttpServletRequest
     *
     * @return a mapping with parameter names and values.
     */
    private Map<String, String> getRequestParameters(HttpServletRequest request) {
        Map<String, String> parameters     = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName  = parameterNames.nextElement();
            String paramValue = request.getParameter(paramName);
            parameters.put(paramName, paramValue);
        }
        return parameters;
    }

    private String getStringValue(byte[] contentAsByteArray, String characterEncoding) {
        try {
            return new String(contentAsByteArray, 0, contentAsByteArray.length, characterEncoding);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
}
