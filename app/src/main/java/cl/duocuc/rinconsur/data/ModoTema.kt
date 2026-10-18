package cl.duocuc.rinconsur.data

// Preferencia de tema: seguir al sistema o forzar claro/oscuro. Se guarda en SharedPreferences.
enum class ModoTema(val etiqueta: String) {
    SISTEMA("Sistema"),
    CLARO("Claro"),
    OSCURO("Oscuro")
}