package gemini.aventura.model;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class SesionAventura {
    private AventuraRequest datos;
    private int turnoActual;
    private List<Turno> turnos;
}

