package gemini.aventura.service;

import gemini.aventura.model.AventuraRequest;
import gemini.aventura.model.AventuraResponse;
import gemini.aventura.model.SesionAventura;
import gemini.aventura.model.Turno;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AventuraService {

    private final GeminiService geminiService;
    private final ImageGenerationService imageGenerationService;

    @Value("classpath:templates/turno-aventura.st")
    private Resource turnoAventuraPrompt;

    private final Map<String, SesionAventura> sesiones = new ConcurrentHashMap<>();

    public AventuraResponse crearAventura(AventuraRequest request) {
        String id = UUID.randomUUID().toString();
        SesionAventura sesion = new SesionAventura();
        sesion.setDatos(request);
        sesion.setTurnoActual(1);
        sesion.setTurnos(new ArrayList<>());


        Turno primerTurno = generarTurno(sesion, null);
        primerTurno.setEsUltimo(request.getCantidadTurnos() == 1);
        sesion.getTurnos().add(primerTurno);

        sesiones.put(id, sesion);

        return new AventuraResponse(id, primerTurno);
    }

    public Turno avanzarTurno(String id, int opcionElegida) {
        SesionAventura sesion = sesiones.get(id);
        if (sesion == null) {
            throw new IllegalArgumentException("Sesión no encontrada");
        }

        if (sesion.getTurnoActual() >= sesion.getDatos().getCantidadTurnos()) {
            throw new IllegalStateException("Ya se alcanzó el último turno");
        }

        sesion.setTurnoActual(sesion.getTurnoActual() + 1);

        Turno nuevoTurno = generarTurno(sesion, opcionElegida);
        nuevoTurno.setEsUltimo(sesion.getTurnoActual() == sesion.getDatos().getCantidadTurnos());
        System.out.println(nuevoTurno.isEsUltimo());
        sesion.getTurnos().add(nuevoTurno);
        getHistoriaCompleta(id);
        System.out.println(sesion.getDatos().toString());
        if(nuevoTurno.isEsUltimo()){
            imageGenerationService.generateImages("una imagen epica de: "+sesion.getDatos().getPersonaje()+"que esta en "+sesion.getDatos().getUbicacion()+" y la historia es de genero "+sesion.getDatos().getGenero(), null);
        }
        return nuevoTurno;
    }

    public String getHistoriaCompleta(String id) {
        SesionAventura sesion = sesiones.get(id);
        if (sesion == null) {
            throw new IllegalArgumentException("Sesión no encontrada");
        }

        return sesion.getTurnos().stream()
                .map(Turno::getTextoHistoria)
                .collect(Collectors.joining("\n"));
    }

    private Turno generarTurno(SesionAventura sesion, Integer opcionElegida) {
        String prompt = construirPromptDesdeTemplate(sesion, opcionElegida);
        String respuesta = geminiService.askGemini(prompt);

        Turno turno = new Turno();
        turno.setNumero(sesion.getTurnoActual());
        turno.setTextoHistoria(respuesta);
        turno.setOpciones(generarOpcionesFicticias(sesion.getDatos().getDecisionesPorTurno())); // TEMP
        return turno;
    }

    private String construirPromptDesdeTemplate(SesionAventura sesion, Integer opcionElegida) {
        String historiaPrev = sesion.getTurnos().stream()
                .map(Turno::getTextoHistoria)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("\n"));

        Map<String, Object> vars = new HashMap<>();
        vars.put("personaje", sesion.getDatos().getPersonaje());
        vars.put("genero", sesion.getDatos().getGenero());
        vars.put("ubicacion", sesion.getDatos().getUbicacion());
        vars.put("turnoActual", sesion.getTurnoActual());
        vars.put("cantidadTurnos", sesion.getDatos().getCantidadTurnos());
        vars.put("historia", historiaPrev.isBlank() ? "(ninguna)" : historiaPrev);
        vars.put("opcionElegida", opcionElegida == null ? "(inicio)" : opcionElegida);
        vars.put("decisionesPorTurno", sesion.getDatos().getDecisionesPorTurno());

        return renderTemplate(turnoAventuraPrompt, vars);
    }

    private String renderTemplate(Resource templateResource, Map<String, Object> vars) {
        try {
            String template = new String(templateResource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            for (Map.Entry<String, Object> e : vars.entrySet()) {
                String placeholder = "{" + e.getKey() + "}";
                template = template.replace(placeholder, String.valueOf(e.getValue()));
            }
            return template;
        } catch (IOException ex) {
            throw new RuntimeException("No se pudo leer la plantilla de prompt", ex);
        }
    }

    private List<String> generarOpcionesFicticias(int cantidad) {
        List<String> opciones = new ArrayList<>();
        for (int i = 1; i <= cantidad; i++) {
            opciones.add("Opción " + i);
        }
        return opciones;
    }
}
