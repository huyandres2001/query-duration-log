package org.example.querydurationlogging.common;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public class Logging{
        public static final String TRACE_ID = "traceId";

        public static final String START_TIME = "startTime";

        public static final String TYPE = "type";

        public static final String DURATION = "duration";

        public static final String RESPONSE_CODE = "responseCode";

        public static final String HTTP_RESPONSE = "httpResponse";

        public static final String HTTP_REQUEST = "httpRequest";

        public static final String REQUEST_DURATION_KEY = "requestDuration";

        public static final String HTTP_REQUEST_START_TIME = "httpRequestStartTime";

        public static final String HTTP_REQUEST_DURATION = "httpRequestDuration";

        public static final String HTTP_METHOD = "httpMethod";

        public static final String HTTP_PATH = "httpPath";

        public static final String QUERY_METHOD = "queryMethod";
    }
}
