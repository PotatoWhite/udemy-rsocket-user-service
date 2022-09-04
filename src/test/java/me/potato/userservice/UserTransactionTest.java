package me.potato.userservice;

import io.rsocket.transport.netty.client.TcpClientTransport;
import me.potato.userservice.dto.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.test.StepVerifier;

import java.util.stream.Stream;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserTransactionTest {
    private RSocketRequester         requester;
    @Autowired
    private RSocketRequester.Builder builder;

    @BeforeAll
    public void setRequester() {
        requester = builder.transport(TcpClientTransport.create("localhost", 7071));
    }


    @ParameterizedTest
    @MethodSource("testData")
    void transactionTest(int amount, TransactionType type, TransactionStatus status) {
        var userDto    = getRandomUser();
        var requestDto = new TransactionRequest(userDto.getId(), amount, type);
        var mono = requester.route("user.transaction")
                .data(requestDto)
                .retrieveMono(TransactionResponse.class)
                .doOnNext(System.out::println);

        StepVerifier.create(mono)
                .expectNextMatches(response -> response.getStatus().equals(status))
                .verifyComplete();
    }

    private Stream<Arguments> testData() {
        return Stream.of(
                Arguments.of(2000, TransactionType.CREDIT, TransactionStatus.COMPLETED),
                Arguments.of(2000, TransactionType.DEBIT, TransactionStatus.COMPLETED),
                Arguments.of(12000, TransactionType.DEBIT, TransactionStatus.FAILED)
        );
    }


    private UserDto getRandomUser() {
        return requester.route("user.all")
                .retrieveFlux(UserDto.class)
                .next()
                .block();
    }
}

