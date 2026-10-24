package br.unip.erp.controller;

import br.unip.erp.dao.DashboardDAO;
import br.unip.erp.model.DashboardDTO;

public class DashboardController {
  private final DashboardDAO dao = new DashboardDAO();

  public DashboardDTO carregar() {
    return dao.carregar();
  }
}
