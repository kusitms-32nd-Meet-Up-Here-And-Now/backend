package com.meetup.hereandnow.core.presentation;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@JsonPropertyOrder({ "success", "timestamp", "data" })
public class RestResponse<T> extends BaseResponse {

    private T data;

    public RestResponse(T data){
        super(true, LocalDateTime.now());
        this.data = data;
    }
}
