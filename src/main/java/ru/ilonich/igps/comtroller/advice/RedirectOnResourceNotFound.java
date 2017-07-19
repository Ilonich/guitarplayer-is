package ru.ilonich.igps.comtroller.advice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import ru.ilonich.igps.to.ErrorInfo;
import ru.ilonich.igps.utils.MessageUtil;
import ru.ilonich.igps.utils.ValidationUtil;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE + 6)
public class RedirectOnResourceNotFound {

    @Autowired
    private MessageUtil messageUtil;

    private static final String ERR_API_NOT_FOUND = "exception.api.notFound";

    @ExceptionHandler(NoHandlerFoundException.class)
    public Object handleStaticResourceNotFound(final NoHandlerFoundException ex, HttpServletRequest req) {
        if (req.getRequestURI().startsWith("/api"))
            return this.getApiResourceNotFoundBody(ex, req);
        else {
            return "redirect:/404";
        }
    }

    private ResponseEntity<ErrorInfo> getApiResourceNotFoundBody(NoHandlerFoundException ex, HttpServletRequest req) {
        ErrorInfo info = new ErrorInfo(req.getRequestURL(), ex.getClass().getSimpleName(), messageUtil.getMessage(ERR_API_NOT_FOUND));
        return new ResponseEntity<>(info, HttpStatus.NOT_FOUND);
    }
}