//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    static ArrayList<Tarea> listaDeTareas = new ArrayList<>();
    static Scanner scanner = new Scanner(System.in);
    static int proximoId = 1;
    // Constante para el nombre del archivo. Así, si queremos cambiarlo, lo hacemos en un solo lugar.
    private static final String NOMBRE_ARCHIVO = "tareas.txt";

    public static void main(String[] args) {
        // Lo primero que hacemos al arrancar es intentar cargar las tareas guardadas.
        cargarTareasDesdeArchivo();
        iniciarGestor();
    }

    public static void iniciarGestor() {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n==== GESTOR DE TAREAS v2.0 ====");
            System.out.println("1. Agregar nueva tarea");
            System.out.println("2. Listar todas las tareas");
            System.out.println("3. Marcar tarea como completada");
            System.out.println("4. Eliminar tarea por ID");
            System.out.println("5. Listar tareas PENDIENTES"); // <-- Nueva opción!
            System.out.println("6. Salir");
            System.out.print("Elige una opción: ");

            try {
                int opcion = scanner.nextInt();
                scanner.nextLine(); // Limpiamos el buffer

                switch (opcion) {
                    case 1:
                        agregarTarea();
                        break;
                    case 2:
                        listarTareas();
                        break;
                    case 3:
                        marcarComoCompletada();
                        break;
                    case 4:
                        eliminarTarea();
                        break;
                    case 5:
                        // Nueva función que usa lambdas
                        listarTareasPendientes();
                        break;
                    case 6:
                        continuar = false;
                        System.out.println("¡Hasta luego! 👋");
                        break;
                    default:
                        System.out.println("Opción no válida. Inténtalo de nuevo.");
                }
            } catch (InputMismatchException e) {
                // Atrapamos el error si el usuario escribe letras en lugar de números.
                System.out.println("Error: Debes ingresar un número. Intenta de nuevo.");
                scanner.nextLine(); // Fundamental para limpiar la entrada incorrecta del scanner.
            } catch (TareaNoEncontradaException e) {
                // Atrapamos nuestra excepción personalizada y mostramos su mensaje.
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public static void agregarTarea() {
        System.out.println("\n--- Agregar Tarea ---");
        String descripcion;
        // Bucle para validar que la descripción no esté vacía.
        do {
            System.out.print("Escribe la descripción de la tarea: ");
            descripcion = scanner.nextLine().trim(); // Usamos trim() para quitar espacios al inicio y final
            if (descripcion.isEmpty()) {
                System.out.println("La descripción no puede estar vacía. Inténtalo de nuevo.");
            }
        } while (descripcion.isEmpty());

        Tarea nuevaTarea = new Tarea(proximoId++, descripcion);
        listaDeTareas.add(nuevaTarea);

        // Cada vez que agregamos, guardamos la lista completa en el archivo.
        guardarTareasEnArchivo();
        System.out.println("¡Tarea agregada con éxito!");
    }

    public static void listarTareas() {
        System.out.println("\n--- Lista de Todas las Tareas ---");
        if (listaDeTareas.isEmpty()) {
            System.out.println("No hay tareas cargadas.");
        } else {
            listaDeTareas.forEach(tarea -> {
                System.out.println("-------------------------");
                System.out.println(tarea);
            });
            System.out.println("-------------------------");
        }
    }

    public static void marcarComoCompletada() throws TareaNoEncontradaException {
        System.out.println("\n--- Marcar como Completada ---");
        System.out.print("Ingresa el ID de la tarea que completaste: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer

        for (Tarea tarea : listaDeTareas) {
            if (tarea.getId() == id) {
                tarea.setCompletada(true);
                guardarTareasEnArchivo(); // Guardamos los cambios
                System.out.println("¡Genial! Tarea marcada como completada.");
                return; // Salimos del método porque ya la encontramos
            }
        }
        // Si el bucle termina y no la encontramos, lanzamos nuestra excepción.
        throw new TareaNoEncontradaException("No se encontró una tarea con el ID " + id);
    }

    public static void eliminarTarea() throws TareaNoEncontradaException {
        System.out.println("\n--- Eliminar Tarea ---");
        System.out.print("Ingresa el ID de la tarea que querés eliminar: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer

        boolean eliminada = listaDeTareas.removeIf(tarea -> tarea.getId() == id);

        if (eliminada) {
            guardarTareasEnArchivo(); // Guardamos los cambios
            System.out.println("Tarea eliminada correctamente.");
        } else {
            // Si removeIf no encontró nada para borrar, lanzamos la excepción.
            throw new TareaNoEncontradaException("No se pudo eliminar la tarea con ID " + id + " porque no existe.");
        }
    }

    // --- NUEVO MÉTODO CON LAMBDA ---
    public static void listarTareasPendientes() {
        System.out.println("\n--- Lista de Tareas Pendientes ---");
        // Usamos la API Stream de Java.
        // 1. .stream() convierte la lista en un "flujo" de datos.
        // 2. .filter() filtra los elementos del flujo. La lambda `tarea -> !tarea.isCompletada()` actúa como el filtro.
        // 3. .forEach() ejecuta una acción por cada elemento que pasó el filtro.
        listaDeTareas.stream()
                .filter(tarea -> !tarea.isCompletada())
                .forEach(tarea -> {
                    System.out.println("-------------------------");
                    System.out.println(tarea);
                });
        System.out.println("-------------------------");
    }

    // --- NUEVOS MÉTODOS DE PERSISTENCIA ---
    private static void guardarTareasEnArchivo() {
        // Usamos try-with-resources para que el archivo se cierre solo, incluso si hay un error.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NOMBRE_ARCHIVO))) {
            for (Tarea tarea : listaDeTareas) {
                writer.write(tarea.toFileString());
                writer.newLine(); // Agrega un salto de línea
            }
        } catch (IOException e) {
            System.out.println("Error: No se pudo guardar el archivo de tareas. " + e.getMessage());
        }
    }

    private static void cargarTareasDesdeArchivo() {
        File archivo = new File(NOMBRE_ARCHIVO);
        if (!archivo.exists()) {
            System.out.println("No se encontró archivo de tareas, empezando de cero.");
            return; // Si no hay archivo, no hay nada que cargar.
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(NOMBRE_ARCHIVO))) {
            String linea;
            int maxId = 0;
            while ((linea = reader.readLine()) != null) {
                Tarea tarea = Tarea.fromFileString(linea);
                listaDeTareas.add(tarea);
                if (tarea.getId() > maxId) {
                    maxId = tarea.getId(); // Buscamos el ID más alto para no repetirlo
                }
            }
            proximoId = maxId + 1; // Actualizamos el contador de IDs
            System.out.println("Se cargaron " + listaDeTareas.size() + " tareas desde el archivo.");
        } catch (IOException e) {
            System.out.println("Error: No se pudo leer el archivo de tareas. " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: El formato del archivo de tareas es incorrecto. " + e.getMessage());
        }
    }
}