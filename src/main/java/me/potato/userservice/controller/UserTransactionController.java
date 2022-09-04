package me.potato.userservice.controller;

import lombok.RequiredArgsConstructor;
import me.potato.userservice.dto.TransactionRequest;
import me.potato.userservice.dto.TransactionResponse;
import me.potato.userservice.service.UserTransactionService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Controller
@MessageMapping("user")
public class UserTransactionController {
    private final UserTransactionService transactionService;

    @MessageMapping("transaction")
    public Mono<TransactionResponse> transaction(Mono<TransactionRequest> request) {
        return request.flatMap(transactionService::doTransaction);
    }

}
