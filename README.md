# 🧙‍♂️ API de Aventuras Interactivas con Gemini

Esta es una **API REST** que genera aventuras interactivas usando **Google Gemini**.  
El usuario define el **género**, **personaje principal**, **cantidad de turnos** y **número de decisiones por turno**.  
Durante la historia, el usuario elige opciones en cada turno y, al final, se genera **una imagen representativa** y un **resumen en PDF** descargable.
Demo de uso: https://youtu.be/ASvpo0Bl6VE
---

## 🚀 Características

✅ Creación de aventuras dinámicas basadas en IA.  
✅ Elección interactiva en cada turno.  
✅ Generación automática de imágenes según el desenlace.  
✅ Descarga del resumen de la historia en **PDF**.  
✅ Integración con **Google Gemini API**.

---

## 🛠️ Tecnologías utilizadas

- **Java 21**
- **Spring Boot** (REST API)
- **Lombok**
- **Google Gemini API**
- **Maven**

---

## 📂 Estructura principal del proyecto

src/main/java/gemini/aventura/
├── controller/
│ └── AventuraController.java # Controlador REST principal
├── model/
│ ├── AventuraRequest.java
│ ├── AventuraResponse.java
│ └── Turno.java
└── service/
├── AventuraService.java # Lógica de creación y avance de aventura
├── ImageGenerationService.java # Generación de imágenes
└── ImageStorageService.java # Manejo de almacenamiento de imágenes


---

## 📡 Endpoints principales

| Método | Endpoint                     | Descripción |
|-------|-----------------------------|-------------|
| `POST` | `/aventura` | Crea una nueva aventura a partir de un `AventuraRequest`. |
| `POST` | `/aventura/{id}/turno?opcionElegida={n}` | Avanza un turno eligiendo la opción seleccionada. |
| `GET`  | `/aventura/{id}/historia` | Devuelve el texto completo de la aventura hasta el momento. |
| `POST` | `/aventura/generateImagen` | Genera imágenes a partir de un prompt (usa Gemini/imagen AI). |
| `GET`  | `/aventura/ultimaImagen` | Devuelve en PNG la última imagen generada. |

---

## 🔑 Configuración de la API Key

Para usar la API de Gemini, debes definir la variable de entorno `GOOGLE_API` en IntelliJ:

1. Abre **Run → Edit Configurations**
2. Selecciona tu configuración de ejecución de Spring Boot.
3. En la sección **Environment Variables**, agrega:
4. Guarda y ejecuta la aplicación.

---

## 📦 Para usar ir a http://localhost:8080/aventura.html





