package com.project.order.client;

import com.project.order.dto.ApiResponse;
import com.project.order.dto.client.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/api/v1/users/{id}")
    ApiResponse<UserDto> getUserById(@PathVariable("id") Long id);
}
