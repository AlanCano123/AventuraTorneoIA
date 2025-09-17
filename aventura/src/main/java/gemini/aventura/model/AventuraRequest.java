package gemini.aventura.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AventuraRequest {
    private String personaje;
    private String ubicacion;
    private int cantidadTurnos;
    private int decisionesPorTurno;
    private String genero;
}

