package me.potato.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
public class TransactionResponse {
    private String            userId;
    private int               amount;
    private TransactionType   type;
    private TransactionStatus status;
}