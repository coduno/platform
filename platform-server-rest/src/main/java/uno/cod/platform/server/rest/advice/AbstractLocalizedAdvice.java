package uno.cod.platform.server.rest.advice;


import com.google.common.base.Throwables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;
import uno.cod.platform.server.core.dto.ExceptionDto;
import uno.cod.platform.server.core.exception.CodunoException;

public abstract class AbstractLocalizedAdvice {
    @Autowired
    private final ResourceBundleMessageSource messageSource;

    public AbstractLocalizedAdvice(@Qualifier("exceptionMessageSource") ResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String getMessage(CodunoException ex, WebRequest request) {
        return messageSource.getMessage(Throwables.getRootCause(ex).getMessage(), ex.getArgs(), request.getLocale());
    }

    ResponseEntity<ExceptionDto> buildResponse(CodunoException ex, WebRequest request, HttpStatus status) {
        return new ResponseEntity<>(new ExceptionDto(getMessage(ex, request)), status);
    }
}
