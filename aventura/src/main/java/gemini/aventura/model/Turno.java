package gemini.aventura.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Turno {
    private int numero;
    private String textoHistoria;
    private List<String> opciones;
    private boolean esUltimo;
}
