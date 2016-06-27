package uno.cod.platform.server.rest.advice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import uno.cod.platform.server.core.dto.ExceptionDto;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice(annotations = RestController.class)
public class MethodArgumentNotValidAdvice {

    private final ResourceBundleMessageSource messageSource;

    @Autowired
    public MethodArgumentNotValidAdvice(@Qualifier("exceptionMessageSource") ResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private String getMessage(String code, WebRequest request) {
        try {
            return messageSource.getMessage(code, null, request.getLocale());
        } catch (NoSuchMessageException e) {
            return null;
        }
    }


    /**
     * Thrown on spring validation errors
     *
     * @param ex validation error exception
     * @return list of errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ExceptionDto> processValidationError(MethodArgumentNotValidException ex, WebRequest req) {
        BindingResult result = ex.getBindingResult();
        List<ExceptionDto> validationMessages = new ArrayList<>();
        for (FieldError fieldError: result.getFieldErrors()) {
            String message = findMessage(fieldError.getCodes(), req);
            if (message == null) {
                message = fieldError.getField() + " " + fieldError.getDefaultMessage();
            }
            validationMessages.add(new ExceptionDto(message));
        }
        return validationMessages;
    }

    private String findMessage(String[] codes, WebRequest request) {
        String message;
        for (String code: codes) {
            if ((message = getMessage(code, request)) != null) {
                return message;
            }
        }
        return null;
    }
}
