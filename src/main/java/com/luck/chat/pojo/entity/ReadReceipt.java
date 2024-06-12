package com.luck.chat.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReadReceipt implements Serializable {

    private Long userId;

    private Long friendId;

    private LocalDateTime readTime;
}
