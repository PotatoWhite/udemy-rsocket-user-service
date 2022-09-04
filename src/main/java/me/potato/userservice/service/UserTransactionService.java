package me.potato.userservice.service;

import lombok.RequiredArgsConstructor;
import me.potato.userservice.dto.TransactionRequest;
import me.potato.userservice.dto.TransactionResponse;
import me.potato.userservice.dto.TransactionStatus;
import me.potato.userservice.dto.TransactionType;
import me.potato.userservice.entity.User;
import me.potato.userservice.repository.UserRepository;
import me.potato.userservice.util.EntityDtoUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.function.UnaryOperator;

@RequiredArgsConstructor
@Service
public class UserTransactionService {
    private final UserRepository repository;


    public Mono<TransactionResponse> doTransaction(TransactionRequest request) {
        var operation = TransactionType.CREDIT.equals(request.getType()) ? credit(request) : debit(request);

        return this.repository.findById(request.getUserId())
                .transform(operation)
                .flatMap(this.repository::save)
                .map(s -> EntityDtoUtil.toResponse(request, TransactionStatus.COMPLETED))
                .defaultIfEmpty(EntityDtoUtil.toResponse(request, TransactionStatus.FAILED));
    }

    private UnaryOperator<Mono<User>> credit(TransactionRequest request) {
        return userMono -> userMono.doOnNext(u -> u.setBalance(u.getBalance() + request.getAmount()));
    }

    private UnaryOperator<Mono<User>> debit(TransactionRequest request) {
        return userMono -> userMono
                .filter(u -> u.getBalance() >= request.getAmount())
                .doOnNext(u -> u.setBalance(u.getBalance() - request.getAmount()));
    }
}
