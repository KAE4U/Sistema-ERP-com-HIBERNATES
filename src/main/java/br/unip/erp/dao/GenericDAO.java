package br.unip.erp.dao;

import java.util.List;

public interface GenericDAO<T, ID> {
  ID inserir(T entidade);

  T atualizar(T entidade);

  void excluir(ID id);

  T buscarPorCodigo(ID id);

  List<T> listar();
}
