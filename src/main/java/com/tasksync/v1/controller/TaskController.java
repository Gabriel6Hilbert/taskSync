package com.tasksync.v1.controller;

import com.tasksync.v1.model.mysql.dto.TaskDto;
import com.tasksync.v1.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarefas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/create")
    public ResponseEntity<TaskDto> criar(@RequestBody TaskDto dto) {
        TaskDto nova = taskService.criar(dto);
        return ResponseEntity.ok(nova);
    }

    @GetMapping("/listAll")
    public ResponseEntity<List<TaskDto>> listarTodas() {
        return ResponseEntity.ok(taskService.listarTodas());
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<TaskDto> buscarPorId(@PathVariable Long id) {
        return taskService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<TaskDto> atualizar(@PathVariable Long id, @RequestBody TaskDto dto) {
        TaskDto atualizado = taskService.atualizar(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        taskService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskDto>> listarPorStatus(@PathVariable String status) {
        return ResponseEntity.ok(taskService.listarPorStatus(status));
    }

    @GetMapping("/prioridade/{prioridade}")
    public ResponseEntity<List<TaskDto>> listarPorPrioridade(@PathVariable String prioridade) {
        return ResponseEntity.ok(taskService.listarPorPrioridade(prioridade));
    }
}
