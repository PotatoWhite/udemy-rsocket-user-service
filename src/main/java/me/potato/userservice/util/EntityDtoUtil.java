package me.potato.userservice.util;

import me.potato.userservice.dto.TransactionRequest;
import me.potato.userservice.dto.TransactionResponse;
import me.potato.userservice.dto.TransactionStatus;
import me.potato.userservice.dto.UserDto;
import me.potato.userservice.entity.User;
import org.springframework.beans.BeanUtils;

public class EntityDtoUtil {
    public static UserDto toDto(User user) {
        var userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);
        return userDto;
    }

    public static User toEntity(UserDto userDto) {
        var user = new User();
        BeanUtils.copyProperties(userDto, user);
        return user;
    }

    public static TransactionResponse toResponse(TransactionRequest request, TransactionStatus status) {
        var response = new TransactionResponse();
        response.setUserId(request.getUserId());
        response.setType(request.getType());
        response.setAmount(request.getAmount());
        response.setStatus(status);

        return response;
    }
}
