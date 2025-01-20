package com.urbaneats.response;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseBody {

    boolean success;
    String message;
    Object data;
    Error error;

    @Getter
    public static ResponseBodyBuilder ResponseBodyBuilder = new ResponseBodyBuilder();
}
