package com.yohan.bank.controller;

import com.yohan.bank.dto.ClientRequestDTO;
import com.yohan.bank.dto.ClientResponseDTO;
import com.yohan.bank.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {
    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<ClientResponseDTO> createClient(@RequestBody @Valid ClientRequestDTO requestDTO) {
        ClientResponseDTO response = clientService.createClient(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable @Valid Long id) {
        clientService.deleteClientById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Void> updateClient(@PathVariable Long id, @RequestBody @Valid ClientRequestDTO clientDTO) {
        clientService.updateClient(id, clientDTO);
        return ResponseEntity.noContent().build();
    }
}
