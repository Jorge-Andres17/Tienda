package com.bigjohn.comun;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class RepositorioMemoria<T> implements Repositorio<T> {
    private final Map<Integer, T> datos = new LinkedHashMap<>();
    private final Function<T, Integer> idDe;

    public RepositorioMemoria(Function<T, Integer> idDe) { this.idDe = idDe; }

    @Override public void guardar(T e) { datos.put(idDe.apply(e), e); }
    @Override public Optional<T> buscarPorId(int id) { return Optional.ofNullable(datos.get(id)); }
    @Override public List<T> listar() { return new ArrayList<>(datos.values()); }
}
