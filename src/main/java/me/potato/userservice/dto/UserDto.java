package me.potato.userservice.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserDto {
    private String id;
    private String name;
    private int    balance;
}
