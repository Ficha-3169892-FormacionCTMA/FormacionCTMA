# Reglas de R8/ProGuard para Mi Formación CTMA.

# CA-09: Eliminar logs en producción (prodRelease)
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}
