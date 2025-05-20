package com.tasksync.v1.controller;

import com.tasksync.v1.integration.GoogleCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calendar")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GoogleCalendarController {

    private final GoogleCalendarService googleCalendarService;

//    @PostMapping("/evento")
//    public ResponseEntity<String> criarEventoManual() {
//        try {
//            googleCalendarService.criarEvento();
//            return ResponseEntity.ok("Evento criado com sucesso no Google Calendar.");
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body("Erro ao criar evento: " + e.getMessage());
//        }
//    }
}
