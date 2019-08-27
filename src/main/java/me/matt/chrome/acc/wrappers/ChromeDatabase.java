package me.matt.chrome.acc.wrappers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import me.matt.chrome.acc.exception.DatabaseConnectionException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class ChromeDatabase {

	private SessionFactory factory;

	private ChromeDatabase() {
	}

	private static class ChromeDatabaseManagerHolder {
		private static final ChromeDatabase instance = new ChromeDatabase();
	}

	public static ChromeDatabase getInstance() {
		try {
			return ChromeDatabaseManagerHolder.instance;
		} catch (ExceptionInInitializerError ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public void connect(final File database) {
		try {
			Configuration conf = new Configuration().configure();
			conf.setProperty("hibernate.connection.url", "jdbc:sqlite://" + getTempDbCopy(database).toAbsolutePath());
			factory = conf.buildSessionFactory();
		} catch (Exception ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	private static Path getTempDbCopy(final File database) throws DatabaseConnectionException {
		Path result;
		try {
			result = Files.createTempFile("CHROME_LOGIN_", null);
			final FileOutputStream out = new FileOutputStream(result.toFile());
			Files.copy(Paths.get(database.getPath()), out);
			out.close();
			result.toFile().deleteOnExit();
		} catch (final IOException e) {
			throw new DatabaseConnectionException("Error copying database! Does the login file exist?");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public List<ChromeLogin> selectAccounts() {
		Session session = factory.openSession();
		Transaction tx = null;
		List<ChromeLogin> applications = new ArrayList<>();

		try {
			tx = session.beginTransaction();
			applications.addAll(session.createQuery("FROM ChromeLogin").list());
			tx.commit();
		} catch (HibernateException e) {
			if (tx != null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return applications;
	}

}