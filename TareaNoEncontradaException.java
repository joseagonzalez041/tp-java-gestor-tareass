// Esta es nuestra propia clase de error. La usaremos cuando no encontremos una tarea.
// Hereda de 'Exception', que es la clase base para errores controlados en Java.
public class TareaNoEncontradaException extends Exception {

    // Este es el constructor. Llama al constructor de la clase padre (Exception)
    // para guardar el mensaje de error que le pasemos.
    public TareaNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}
