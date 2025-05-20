package com.tasksync.v1.model.mysql.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "task_init")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "descricao", nullable = false, length = 255)
    private String descricao;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "prioridade", nullable = false, length = 20)
    private String prioridade;

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "responsavel", length = 100)
    private String responsavel;

    @Column(name = "concluida", nullable = false)
    private Boolean concluida;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "google_event_id")
    private String googleEventId;

    @Column(name = "alerta_em")
    private LocalDateTime alertaEm;

    @Column(name = "link", length = 512)
    private String link;
}


