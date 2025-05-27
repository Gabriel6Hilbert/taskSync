package com.tasksync.v1.model.mysql.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//release v2.0.0
public class TaskDto {

    private Long id;

    private String descricao;

    private LocalDateTime dataHora;

    private String status;

    private String prioridade;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String responsavel;

    private Boolean concluida;

    private String observacoes;

    private LocalDateTime alertaEm;

    private String linkCalendar;
}
