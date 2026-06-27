package com.thetruecolonel.dbridge.models;

import com.github.bsideup.jabel.Desugar;

@Desugar
public record DiscordAuthor(String username, boolean bot) { }
