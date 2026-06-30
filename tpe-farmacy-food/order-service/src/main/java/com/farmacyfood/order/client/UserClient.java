package com.farmacyfood.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", path = "/api/v1/usuarios")
@Profile("!dev")
public interface UserClient {

    @GetMapping("/{id}")
    UserResponseDTO getUser(@PathVariable("id") Long userId);

    @GetMapping("/username/{username}")
    UserResponseDTO getUserByUsername(@PathVariable String username);

}
