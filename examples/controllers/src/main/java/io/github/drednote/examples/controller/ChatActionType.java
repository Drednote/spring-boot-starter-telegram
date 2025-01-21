package io.github.drednote.examples.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatActionType {
    TYPING("typing");

    private final String value;
}