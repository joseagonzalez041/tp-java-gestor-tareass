import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Tarea {
    private int id;
    private String descripcion;
    private boolean completada;
    private LocalDate fechaCreacion;

    // Constructor que usamos cuando creamos una tarea nueva desde el programa
    public Tarea(int id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
        this.completada = false;
        this.fechaCreacion = LocalDate.now();
    }

    // Constructor que usamos internamente para reconstruir una Tarea desde el archivo .txt
    private Tarea(int id, String descripcion, boolean completada, LocalDate fechaCreacion) {
        this.id = id;
        this.descripcion = descripcion;
        this.completada = completada;
        this.fechaCreacion = fechaCreacion;
    }

    // --- Getters y Setters (sin cambios) ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public boolean isCompletada() { return completada; }
    public void setCompletada(boolean completada) { this.completada = completada; }
    public LocalDate getFechaCreacion() { return fechaCreacion; }

    // El toString() sigue sirviendo para mostrar la tarea linda en la consola
    @Override
    public String toString() {
        String estado = completada ? "Completada" : "Pendiente";
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return "ID Tarea: " + id +
                "\n  Descripción: " + descripcion +
                "\n  Estado: " + estado +
                "\n  Creada el: " + fechaCreacion.format(formato);
    }

    // --- MÉTODOS NUEVOS PARA PERSISTENCIA ---

    // Este método convierte el objeto Tarea a una línea de texto para guardarla.
    public String toFileString() {
        // Formato: id,descripcion,completada,fechaCreacion
        return id + "," + descripcion + "," + completada + "," + fechaCreacion;
    }

    // Este método "estático" crea un objeto Tarea a partir de una línea de texto del archivo.
    public static Tarea fromFileString(String fileString) {
        String[] partes = fileString.split(",", 4); // Dividimos la línea por las comas
        int id = Integer.parseInt(partes[0]);
        String descripcion = partes[1];
        boolean completada = Boolean.parseBoolean(partes[2]);
        LocalDate fecha = LocalDate.parse(partes[3]);
        return new Tarea(id, descripcion, completada, fecha);
    }
}
