package com.tecnocampus.LS2.protube_back.adapter.in.dto;

public record RegisterRequest(
         String username,
         String email,
         String passwordhash) {
}
