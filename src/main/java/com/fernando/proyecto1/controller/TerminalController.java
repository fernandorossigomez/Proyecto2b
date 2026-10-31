
package com.fernando.proyecto1.controller;

import com.fernando.proyecto1.entity.TerminalEntity;
import com.fernando.proyecto1.repository.TerminalRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/terminals")
public class TerminalController {

    private final TerminalRepository terminalRepository;

    public TerminalController(TerminalRepository terminalRepository) {
        this.terminalRepository = terminalRepository;
    }

    @GetMapping
    public List<TerminalEntity> getAllTerminals() {
        return terminalRepository.findAll()
                .stream()
                .sorted((a, b) -> a.getId().compareTo(b.getId()))
                .toList();
    }
}
