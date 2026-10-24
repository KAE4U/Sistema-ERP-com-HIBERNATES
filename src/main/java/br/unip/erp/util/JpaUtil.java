package br.unip.erp.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/** Bootstrap único do EntityManagerFactory, configurável por arquivo e ambiente. */
public final class JpaUtil {
  private static volatile EntityManagerFactory emf;

  private JpaUtil() {}

  public static EntityManagerFactory getEntityManagerFactory() {
    if (emf == null)
      synchronized (JpaUtil.class) {
        if (emf == null) emf = Persistence.createEntityManagerFactory("erpPU", propriedades());
      }
    return emf;
  }

  public static EntityManager getEntityManager() {
    return getEntityManagerFactory().createEntityManager();
  }

  public static void close() {
    if (emf != null && emf.isOpen()) emf.close();
  }

  private static Map<String, Object> propriedades() {
    Properties p = new Properties();
    try (InputStream in = JpaUtil.class.getResourceAsStream("/config.properties")) {
      if (in != null) p.load(in);
    } catch (IOException e) {
      throw new IllegalStateException("Falha ao ler config.properties", e);
    }
    Path local = Path.of("config.local.properties");
    if (Files.isRegularFile(local))
      try (InputStream in = Files.newInputStream(local)) {
        p.load(in);
      } catch (IOException e) {
        throw new IllegalStateException("Falha ao ler config.local.properties", e);
      }
    String url = env("DB_URL", p.getProperty("db.url"));
    String user = env("DB_USER", p.getProperty("db.user"));
    String pass = env("DB_PASSWORD", p.getProperty("db.password"));
    String driver = env("DB_DRIVER", p.getProperty("db.driver", "org.postgresql.Driver"));
    String dialect =
        env("DB_DIALECT", p.getProperty("db.dialect", "org.hibernate.dialect.PostgreSQLDialect"));
    String ddl = env("DB_DDL", p.getProperty("db.ddl", "validate"));
    if (url == null || user == null || pass == null) {
      throw new IllegalStateException("Configure DB_URL, DB_USER e DB_PASSWORD.");
    }
    Map<String, Object> m = new HashMap<>();
    m.put("jakarta.persistence.jdbc.url", url);
    m.put("jakarta.persistence.jdbc.user", user);
    m.put("jakarta.persistence.jdbc.password", pass);
    m.put("jakarta.persistence.jdbc.driver", driver);
    m.put("hibernate.dialect", dialect);
    m.put("hibernate.hbm2ddl.auto", ddl);
    m.put("hibernate.show_sql", env("DB_SHOW_SQL", p.getProperty("db.show_sql", "false")));
    return m;
  }

  private static String env(String key, String fallback) {
    String v = System.getenv(key);
    return v == null || v.isBlank() ? fallback : v;
  }
}
