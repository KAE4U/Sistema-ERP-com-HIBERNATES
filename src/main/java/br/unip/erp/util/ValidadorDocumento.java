package br.unip.erp.util;

/**
 * Conferência simples de CPF/CNPJ para fins acadêmicos: verifica apenas a quantidade de dígitos (11
 * = CPF, 14 = CNPJ). Não calcula dígito verificador, então qualquer número é aceito.
 */
public final class ValidadorDocumento {

  public static final int DIGITOS_CPF = 11;
  public static final int DIGITOS_CNPJ = 14;

  private ValidadorDocumento() {}

  /** Remove qualquer caractere que não seja dígito (pontos, traços, barra). */
  public static String digitos(String s) {
    return s == null ? "" : s.replaceAll("\\D", "");
  }

  /** True se o texto tiver 11 dígitos (CPF) ou 14 dígitos (CNPJ). */
  public static boolean isValido(String documento) {
    int n = digitos(documento).length();
    return n == DIGITOS_CPF || n == DIGITOS_CNPJ;
  }

  /** True se tiver 11 dígitos; false se tiver 14. Use após {@link #isValido}. */
  public static boolean isCpf(String documento) {
    return digitos(documento).length() == DIGITOS_CPF;
  }

  /**
   * Mensagem para o usuário quando a quantidade de dígitos não é 11 nem 14.
   *
   * @return null se o documento for aceito
   */
  public static String motivoInvalido(String documento) {
    if (isValido(documento)) {
      return null;
    }
    return "O documento deve ter 11 dígitos (CPF) ou 14 dígitos (CNPJ).\n"
        + "Foram informados "
        + digitos(documento).length()
        + " dígitos.";
  }
}
