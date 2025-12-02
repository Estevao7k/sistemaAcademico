package model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private Integer cargaHoraria;

    @OneToMany(mappedBy = "curso", cascade = CascadeType.ALL)
    @ToString.Exclude // Evita loop infinito no toString do Lombok
    private List<Disciplina> disciplinas;

    @Override
    public String toString() { return this.nome; } // Para exibir no ComboBox corretamente
}