package com.farmacyfood.user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "order-service")
public interface OrderServiceClient {

    @GetMapping("/api/v1/ordenes/user/{userId}")
    List<?> getOrdersByUserId(@PathVariable("userId") Long userId);
}
