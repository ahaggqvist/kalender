package se.sjuhundrac.kalender.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Slf4j
@Controller
public class CustomErrorController implements ErrorController {
    private final ErrorAttributes errorAttributes;
    private final Environment environment;

    public CustomErrorController(ErrorAttributes errorAttributes, Environment environment) {
        this.errorAttributes = errorAttributes;
        this.environment = environment;
    }

    @RequestMapping(value = "/error", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView errorHtml(HttpServletRequest request, Model model) {
        var view = "error";
        var httpStatus = getHttpStatus(request);
        if (httpStatus != null) {
            if (httpStatus == HttpStatus.INTERNAL_SERVER_ERROR) {
                log.error(getErrorAttributesAsString(request));
                if (StringUtils.isNotBlank(getExceptionMessage(request))) {
                    log.error(getExceptionMessage(request));
                }
                var activeProfiles = environment.getActiveProfiles();
                if (ArrayUtils.contains(activeProfiles, "dev")) {
                    model.addAttribute(
                            "error", getExceptionMessage(request) + " " + getErrorAttributesAsString(request));
                }
                view = "500";
            } else if (httpStatus == HttpStatus.UNAUTHORIZED || httpStatus == HttpStatus.FORBIDDEN) {
                view = "401";
            } else if (httpStatus == HttpStatus.NOT_FOUND) {
                view = "404";
            }
        }
        return new ModelAndView(view);
    }

    @RequestMapping(value = "/error", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> error(HttpServletRequest request) {
        log.error(getErrorAttributesAsString(request));
        if (StringUtils.isNotBlank(getExceptionMessage(request))) {
            log.error(getExceptionMessage(request));
        }
        return new ResponseEntity<>(
                getErrorAttributes(request).get("error") == null
                        ? "Internal Server Error"
                        : getErrorAttributes(request).get("error").toString(),
                HttpStatus.valueOf(
                        Integer.parseInt(
                                getErrorAttributes(request).get("status") == null
                                        ? "500"
                                        : getErrorAttributes(request).get("status").toString())));
    }

    private String getErrorAttributesAsString(HttpServletRequest request) {
        var sb = new StringBuilder();
        getErrorAttributes(request).values().forEach(v -> sb.append(v).append(" "));
        return sb.toString();
    }

    private Map<String, Object> getErrorAttributes(HttpServletRequest request) {
        return errorAttributes.getErrorAttributes(
                new ServletWebRequest(request),
                ErrorAttributeOptions.of(ErrorAttributeOptions.Include.STACK_TRACE));
    }

    private String getExceptionMessage(HttpServletRequest request) {
        var attribute = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        if (attribute != null) {
            return attribute.getMessage();
        }
        return "";
    }

    private HttpStatus getHttpStatus(HttpServletRequest request) {
        Integer errorStatusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (errorStatusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        try {
            return HttpStatus.valueOf(errorStatusCode);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
