package io.github.joaopedro.checkout.api;

import io.github.joaopedro.checkout.payment.InvalidInstallmentsException;
import io.github.joaopedro.checkout.strategy.UnknownStrategyException;
import java.net.URI;
import java.util.Comparator;
import java.util.List;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Converts every client error into an RFC 9457 {@link ProblemDetail} body.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    static final URI UNKNOWN_STRATEGY = URI.create("urn:checkout:problem:unknown-strategy");
    static final URI INVALID_INSTALLMENTS = URI.create("urn:checkout:problem:invalid-installments");
    static final URI VALIDATION = URI.create("urn:checkout:problem:validation");

    @ExceptionHandler(UnknownStrategyException.class)
    ProblemDetail handleUnknownStrategy(UnknownStrategyException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setType(UNKNOWN_STRATEGY);
        problem.setTitle("Unknown strategy");
        problem.setProperty("axis", ex.axis());
        problem.setProperty("invalidValue", ex.requestedKey());
        problem.setProperty("validOptions", ex.validOptions());
        return problem;
    }

    @ExceptionHandler(InvalidInstallmentsException.class)
    ProblemDetail handleInvalidInstallments(InvalidInstallmentsException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problem.setType(INVALID_INSTALLMENTS);
        problem.setTitle("Invalid installments");
        problem.setProperty("paymentMethod", ex.paymentMethod());
        problem.setProperty("maxInstallments", ex.max());
        return problem;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        List<FieldViolation> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(GlobalExceptionHandler::toError)
                .sorted(Comparator.comparing(FieldViolation::field))
                .toList();
        return ResponseEntity.badRequest().body(validationProblem(errors));
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(@NonNull HandlerMethodValidationException ex,
                                                                            @NonNull HttpHeaders headers,
                                                                            @NonNull HttpStatusCode status,
                                                                            @NonNull WebRequest request) {
        // With constraints on @RequestParam, Spring validates the whole method, so @Valid body
        // errors also arrive here as ParameterErrors carrying the original field errors.
        List<FieldViolation> errors = ex.getParameterValidationResults().stream()
                .flatMap(result -> result instanceof ParameterErrors parameterErrors
                        ? parameterErrors.getFieldErrors().stream().map(GlobalExceptionHandler::toError)
                        : result.getResolvableErrors().stream()
                                .map(error -> toError(result.getMethodParameter().getParameterName(), error)))
                .sorted(Comparator.comparing(FieldViolation::field))
                .toList();
        return ResponseEntity.badRequest().body(validationProblem(errors));
    }

    private static ProblemDetail validationProblem(List<FieldViolation> errors) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Request has %d invalid field(s)".formatted(errors.size()));
        problem.setType(VALIDATION);
        problem.setTitle("Validation failed");
        problem.setProperty("errors", errors);
        return problem;
    }

    private static FieldViolation toError(FieldError error) {
        return toError(error.getField(), error);
    }

    private static FieldViolation toError(String field, MessageSourceResolvable error) {
        return new FieldViolation(field, error.getDefaultMessage());
    }

    /** One invalid input: request field (or parameter) name and the constraint message. */
    record FieldViolation(String field, String message) {
    }
}
