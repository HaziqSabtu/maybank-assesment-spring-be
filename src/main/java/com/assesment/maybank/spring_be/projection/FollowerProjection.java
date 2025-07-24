package com.assesment.maybank.spring_be.projection;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class FollowerProjection {
    private UUID id;
    private String username;
}
