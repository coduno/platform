package uno.cod.platform.server.rest.advice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import uno.cod.platform.server.core.dto.ExceptionDto;
import uno.cod.platform.server.core.exception.CodunoException;
import uno.cod.platform.server.core.exception.CodunoNoSuchElementException;

@ControllerAdvice(annotations = RestController.class)
public class NoSuchElementAdvice extends AbstractLocalizedAdvice {

    @Autowired
    public NoSuchElementAdvice(@Qualifier("exceptionMessageSource") ResourceBundleMessageSource messageSource) {
        super(messageSource);
    }

    @ExceptionHandler(value = CodunoNoSuchElementException.class)
    @ResponseBody
    public ResponseEntity<ExceptionDto> handleNoSuchElement(final CodunoException ex, WebRequest request) {
        return buildResponse(ex, request, HttpStatus.NOT_FOUND);
    }
}
