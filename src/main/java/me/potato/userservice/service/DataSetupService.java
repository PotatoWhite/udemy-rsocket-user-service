package me.potato.userservice.service;

import lombok.RequiredArgsConstructor;
import me.potato.userservice.entity.User;
import me.potato.userservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@Service
public class DataSetupService implements CommandLineRunner {
    private final UserRepository repository;

    @Override
    public void run(String... args) throws Exception {
        var user1 = new User("sam", 10000);
        var user2 = new User("mike", 10000);
        var user3 = new User("jake", 10000);

        Flux.just(user1, user2, user3)
                .flatMap(repository::save)
                .doOnNext(System.out::println)
                .subscribe();

    }
}
