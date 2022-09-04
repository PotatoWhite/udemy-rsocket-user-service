package me.potato.userservice.controller;

import lombok.RequiredArgsConstructor;
import me.potato.userservice.dto.OperationType;
import me.potato.userservice.dto.UserDto;
import me.potato.userservice.service.UserService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Controller
@MessageMapping("user")
public class UserController {
    private final UserService userService;

    // rs
    @MessageMapping("all")
    public Flux<UserDto> getAll() {
        return userService.getAll();
    }

    // rr
    @MessageMapping("get.{id}")
    public Mono<UserDto> getById(@DestinationVariable String id) {
        return userService.getById(id);
    }

    // rr
    @MessageMapping("create")
    public Mono<UserDto> create(Mono<UserDto> userDtoMono) {
        return userService.create(userDtoMono);
    }

    // rr
    @MessageMapping("update.{id}")
    public Mono<UserDto> update(@DestinationVariable String id, Mono<UserDto> updateMono) {
        return userService.update(id, updateMono);
    }

    // ff
    @MessageMapping("delete.{id}")
    public Mono<Void> delete(@DestinationVariable String id) {
        return userService.delete(id);
    }

    @MessageMapping("operation.type")
    public Mono<Void> metadataOperationType(@Header("operation-type") OperationType operationType, Mono<UserDto> userDtoMono) {
        System.out.println(operationType);
        userDtoMono.doOnNext(System.out::println).subscribe();
        return Mono.empty();
    }
}
