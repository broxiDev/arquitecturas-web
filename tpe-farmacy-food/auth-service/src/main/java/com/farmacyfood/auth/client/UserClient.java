package com.farmacyfood.auth.client;

import com.farmacyfood.auth.dto.UserRegistrationRequest;
import com.farmacyfood.auth.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", path = "/api/v1/usuarios")
public interface UserClient {
    @PostMapping("/registrar")
    UserResponse register(@RequestBody UserRegistrationRequest request);
}
