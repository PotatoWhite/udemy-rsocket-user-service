package me.potato.userservice;

import io.rsocket.metadata.WellKnownMimeType;
import io.rsocket.transport.netty.client.TcpClientTransport;
import me.potato.userservice.dto.OperationType;
import me.potato.userservice.dto.UserDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.util.MimeTypeUtils;
import reactor.test.StepVerifier;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserCrudTest {

    private RSocketRequester requester;

    @Autowired
    private RSocketRequester.Builder builder;

    @BeforeAll
    public void setRequester() {
        requester = builder.transport(TcpClientTransport.create("localhost", 7071));
    }

    @Test
    void getAllTest() {
        var flux = requester.route("user.all")
                .retrieveFlux(UserDto.class)
                .doOnNext(System.out::println);

        StepVerifier.create(flux)
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void getSingleUserTest() {
        var userDto = getRandomUser();
        var mono = requester.route("user.get.{id}", userDto.getId())
                .retrieveMono(UserDto.class)
                .doOnNext(System.out::println);

        StepVerifier.create(mono)
                .expectNextMatches(user -> user.equals(userDto))
                .verifyComplete();
    }

    private UserDto getRandomUser() {
        return requester.route("user.all")
                .retrieveFlux(UserDto.class)
                .next()
                .block();
    }

    @Test
    void createUserTest() {
        var userDto = new UserDto();
        userDto.setName("test");
        userDto.setBalance(10000);
        var mono = requester.route("user.create")
                .data(userDto)
                .retrieveMono(UserDto.class)
                .doOnNext(System.out::println);

        StepVerifier.create(mono)
                .expectNextMatches(user -> user.getName().equals(userDto.getName()))
                .verifyComplete();
    }

    @Test
    void updateUserTest() {
        var userDto = getRandomUser();
        userDto.setBalance(-10);
        var mono = requester.route("user.update.{id}", userDto.getId())
                .data(userDto)
                .retrieveMono(UserDto.class)
                .doOnNext(System.out::println);

        StepVerifier.create(mono)
                .expectNextMatches(user -> user.getBalance() == -10)
                .verifyComplete();
    }

    @Test
    void deleteUserTest() {
        var userDto = getRandomUser();
        var mono    = requester.route("user.delete.{id}", userDto.getId()).send();

        StepVerifier.create(mono).verifyComplete();

        var flux = requester.route("user.get.{id}", userDto.getId())
                .retrieveMono(UserDto.class)
                .doOnNext(System.out::println);

        StepVerifier.create(flux)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void metadataTest() {
        var mimeType = MimeTypeUtils.parseMimeType(WellKnownMimeType.APPLICATION_CBOR.getString());

        var userDto = new UserDto();
        userDto.setName("md");
        userDto.setBalance(10000);

        var mono = requester.route("user.operation.type")
                .metadata(OperationType.PUT, mimeType)
                .data(userDto)
                .send();

        StepVerifier.create(mono)
                .verifyComplete();
    }

}
