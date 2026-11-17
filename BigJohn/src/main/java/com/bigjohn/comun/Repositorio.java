package com.bigjohn.comun;

import java.util.List;
import java.util.Optional;

/** Contrato de almacenamiento. Hoy es en memoria; despues se puede cambiar por una base de datos. */
public interface Repositorio<T> {
    void guardar(T elemento);
    Optional<T> buscarPorId(int id);
    List<T> listar();
}
