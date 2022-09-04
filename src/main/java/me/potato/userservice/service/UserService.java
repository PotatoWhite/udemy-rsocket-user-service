package me.potato.userservice.service;

import lombok.RequiredArgsConstructor;
import me.potato.userservice.dto.UserDto;
import me.potato.userservice.repository.UserRepository;
import me.potato.userservice.util.EntityDtoUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public Flux<UserDto> getAll() {
        return userRepository.findAll()
                .map(EntityDtoUtil::toDto);
    }

    public Mono<UserDto> getById(String id) {
        return userRepository.findById(id)
                .map(EntityDtoUtil::toDto);
    }

    public Mono<UserDto> create(Mono<UserDto> userDtoMono) {
        return userDtoMono.map(EntityDtoUtil::toEntity)
                .flatMap(userRepository::save)
                .map(EntityDtoUtil::toDto);
    }

    public Mono<UserDto> update(String id, Mono<UserDto> updateMono) {
        return userRepository.findById(id)
                .flatMap(user -> updateMono.map(EntityDtoUtil::toEntity)
                        .doOnNext(update -> update.setId(id)))
                .flatMap(userRepository::save)
                .map(EntityDtoUtil::toDto);
    }

    public Mono<Void> delete(String id) {
        return userRepository.deleteById(id);
    }
}
