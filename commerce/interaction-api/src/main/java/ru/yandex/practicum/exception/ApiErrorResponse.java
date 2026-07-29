package ru.yandex.practicum.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ApiErrorResponse {
    private Cause cause;
    private List<StackTraceElement> stackTrace;
    private String httpStatus;
    private String userMessage;
    private String message;
    private List<Suppressed> suppressed;
    private String localizedMessage;

    @Data
    public static class Cause {
        private List<StackTraceElement> stackTrace;
        private String message;
        private String localizedMessage;
    }

    @Data
    public static class Suppressed {
        private List<StackTraceElement> stackTrace;
        private String message;
        private String localizedMessage;
    }

    @Data
    public static class StackTraceElement {
        @JsonProperty("classLoaderName")
        private String classLoaderName;
        @JsonProperty("moduleName")
        private String moduleName;
        @JsonProperty("moduleVersion")
        private String moduleVersion;
        @JsonProperty("methodName")
        private String methodName;
        @JsonProperty("fileName")
        private String fileName;
        @JsonProperty("lineNumber")
        private Integer lineNumber;
        @JsonProperty("className")
        private String className;
        @JsonProperty("nativeMethod")
        private Boolean nativeMethod;

    }
}
