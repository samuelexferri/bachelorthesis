package se4med.dataregistration;

import static se4med.jooq.Tables.RESULTS;
import static se4med.jooq.Tables.USER;
import static se4med.jooq.Tables.PATIENTDOC;
import static se4med.jooq.Tables.RESULT_NOT_REGISTERED;
import static se4med.jooq.Tables.APPLICATION;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.SQLDialect;
import org.jooq.SelectConditionStep;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;

import se4med.jooq.tables.records.PatientdocRecord;

/**
 * The real database connector
 */
public class DBSe4MedDataConnector implements Se4MedDataConnector {

	public static Se4MedDataConnector instance = new DBSe4MedDataConnector();

	/**
	 * Connect to the database if it exists
	 * 
	 * @return Connection: a connection to the URL
	 */
	static private Connection getConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			return DriverManager.getConnection(Constant.url, Constant.user, Constant.password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 *  OLD METHODS
	 */
	@Override
	public int saveResults(String username, String emailuser, String password, String idApp, Timestamp dateTime,
			String result) {
		Connection conn = getConnection();
		DSLContext database = DSL.using(conn, SQLDialect.MYSQL, new Settings().withExecuteLogging(true));
		SelectConditionStep<Record1<String>> searchuser = database.select(USER.EMAIL).from(USER)
				.where(USER.PASSWORD.eq(password)).and(USER.EMAIL.eq(emailuser));
		SelectConditionStep<Record1<String>> searchapp = null;
		if (searchuser.fetchOne() != null) {
			searchapp = database.select(PATIENTAPP.USERNAME).from(PATIENTAPP).where(PATIENTAPP.IDAPP.eq(idApp))
					.and(PATIENTAPP.USERNAME.eq(username).and(PATIENTAPP.EMAILPATIENT.eq(emailuser)));
			if (searchapp.fetchOne() != null) {
				database.insertInto(RESULTS, RESULTS.IDAPP, RESULTS.EMAILPATIENT, RESULTS.USERNAME, RESULTS.DATEANDTIME,
						RESULTS.RESULT).values(idApp, emailuser, username, dateTime, result).execute();
				return 1; // DATA SAVED
			} else
				return 0; // ERROR
		}
		return 2;
	}

	@Override
	public int saveResults(String username, String emailuser, String idApp, Timestamp dateTime, String result) {
		Connection conn = getConnection();
		DSLContext database = DSL.using(conn, SQLDialect.MYSQL, new Settings().withExecuteLogging(true));
		SelectConditionStep<Record1<String>> searchuser = database.select(PATIENTAPP.USERNAME).from(PATIENTAPP)
				.where(PATIENTAPP.IDAPP.eq(idApp))
				.and(PATIENTAPP.USERNAME.eq(username).and(PATIENTAPP.EMAILPATIENT.eq(emailuser)));
		if (searchuser.fetchOne() != null) {
			database.insertInto(RESULTS, RESULTS.IDAPP, RESULTS.EMAILPATIENT, RESULTS.USERNAME, RESULTS.DATEANDTIME,
					RESULTS.RESULT).values(idApp, emailuser, username, dateTime, result).execute();
			return 1; // DATA SAVED
		} else
			return 0; // ERROR
	}

	@Override
	public int saveAppSettings(String username, String emailuser, String idapp, String appsettings) {
		Connection conn = getConnection();
		DSLContext database = DSL.using(conn, SQLDialect.MYSQL, new Settings().withExecuteLogging(true));
		SelectConditionStep<Record1<String>> searchuser = database.select(PATIENTAPP.USERNAME).from(PATIENTAPP)
				.where(PATIENTAPP.IDAPP.eq(idapp))
				.and(PATIENTAPP.USERNAME.eq(username).and(PATIENTAPP.EMAILPATIENT.eq(emailuser)));
		if (searchuser.fetchOne() != null) {
			database.update(PATIENTAPP).set(PATIENTAPP.SETTINGS, appsettings).where(PATIENTAPP.IDAPP.eq(idapp))
					.and(PATIENTAPP.USERNAME.eq(username).and(PATIENTAPP.EMAILPATIENT.eq(emailuser))).execute();
			return 1; // DATA SAVED
		} else
			return 0; // ERROR
	}

	@Override
	public String getAppSettings(String username, String emailuser, String idapp) {
		Connection conn = getConnection();
		DSLContext database = DSL.using(conn, SQLDialect.MYSQL, new Settings().withExecuteLogging(true));
		SelectConditionStep<Record1<String>> searchuser = database.select(PATIENTAPP.USERNAME).from(PATIENTAPP)
				.where(PATIENTAPP.IDAPP.eq(idapp))
				.and(PATIENTAPP.USERNAME.eq(username).and(PATIENTAPP.EMAILPATIENT.eq(emailuser)));
		if (searchuser.fetchOne() != null) {
			SelectConditionStep<Record1<String>> settings = database.select(PATIENTAPP.SETTINGS).from(PATIENTAPP)
					.where(PATIENTAPP.IDAPP.eq(idapp))
					.and(PATIENTAPP.USERNAME.eq(username).and(PATIENTAPP.EMAILPATIENT.eq(emailuser)));
			if (settings.fetchOne() != null)
				for (Record1<String> setting : settings)
					return (String) setting.get(0);
			else
				return "";
		}
		return "";
	}

	@Override
	public int findDoctor(String email, String password) {
		Connection conn = getConnection();
		DSLContext database = DSL.using(conn, SQLDialect.MYSQL, new Settings().withExecuteLogging(true));
		SelectConditionStep<Record1<String>> searchuser = database.selectDistinct(USER.EMAIL).from(USER).join(DOCTOR)
				.on(USER.EMAIL.eq(DOCTOR.EMAIL)).where(USER.EMAIL.eq(email)).and(USER.PASSWORD.eq(password));
		if (searchuser.fetchOne() != null)
			return 1; // User found
		else
			return 0; // User not found
	}

	@Override
	public ArrayList<String> findPatient(String email, String idapp, String password) {
		Connection conn = getConnection();
		DSLContext database = DSL.using(conn, SQLDialect.MYSQL, new Settings().withExecuteLogging(true));
		SelectConditionStep<Record1<String>> searchuser = database.select(USER.EMAIL).from(USER)
				.where(USER.PASSWORD.eq(password)).and(USER.EMAIL.eq(email));
		SelectConditionStep<Record1<String>> searchuserlist = null;
		ArrayList<String> userlist = new ArrayList<>();
		if (searchuser.fetchOne() != null) {
			searchuserlist = database.select(PATIENTAPP.USERNAME).from(PATIENTAPP).where(PATIENTAPP.IDAPP.eq(idapp))
					.and(PATIENTAPP.EMAILPATIENT.eq(email));
			for (Record1<String> singleuser : searchuserlist)
				userlist.add((String) singleuser.get(0));
		}
		return userlist;
	}

	@Override
	public int findUser(String emailuser, String password) {
		Connection conn = getConnection();
		DSLContext database = DSL.using(conn, SQLDialect.MYSQL, new Settings().withExecuteLogging(true));
		SelectConditionStep<Record1<String>> searchuser = database.selectDistinct(USER.EMAIL).from(USER)
				.where(USER.EMAIL.eq(emailuser)).and(USER.PASSWORD.eq(password));
		if (searchuser.fetchOne() != null)
			return 1; // User found
		else
			return 0; // User not found
	}
	
	/**
	 *  NEW METHODS
	 */
	
	// NEW
	@Override
	public String getNameSurnameDoctor(String emaildoc, String password, String idapp) {
		Connection conn = getConnection();
		DSLContext database = DSL.using(conn, SQLDialect.MYSQL, new Settings().withExecuteLogging(true));

		SelectConditionStep<Record2<String, String>> ns = database
				.selectDistinct(USER.NAME.as("name"), USER.SURNAME.as("surname")).from(USER)
				.where(USER.EMAIL.eq(emaildoc)).and(USER.PASSWORD.eq(password));
		SelectConditionStep<Record1<String>> searchapp = database.select(APPLICATION.ID).from(APPLICATION)
				.where(APPLICATION.ID.eq(idapp));

		if (ns.fetchOne() != null && searchapp.fetchOne() != null) {
			for (Record2<String, String> x : ns)
				return (String) "name:" + x.get("name") + ",surname:" + x.get("surname");
		}
		return "";
	}

	// NEW
	@Override
	public ArrayList<String> getPatientDocList(String emaildoc, String password, String idapp) {
		Connection conn = getConnection();
		DSLContext database = DSL.using(conn, SQLDialect.MYSQL, new Settings().withExecuteLogging(true));

		SelectConditionStep<Record1<String>> searchuser = database.select(USER.EMAIL).from(USER)
				.where(USER.EMAIL.eq(emaildoc).and(USER.PASSWORD.eq(password)));
		SelectConditionStep<Record1<String>> searchapp = database.select(APPLICATION.ID).from(APPLICATION)
				.where(APPLICATION.ID.eq(idapp));

		SelectConditionStep<Record3<UInteger, String, String>> searchlist = null;
		ArrayList<String> list = new ArrayList<>();

		if (searchuser.fetchOne() != null && searchapp.fetchOne() != null) {
			searchlist = database
					.select(PATIENTDOC.ID.as("id"), PATIENTDOC.NAME.as("name"), PATIENTDOC.SURNAME.as("surname"))
					.from(PATIENTDOC).where(PATIENTDOC.EMAILDOC.eq(emaildoc));
			for (Record3<UInteger, String, String> singleuser : searchlist)
				list.add((String) "{id:" + singleuser.get("id") + ",name:" + singleuser.get("name") + ",surname:"
						+ singleuser.get("surname") + "}");
		}
		return list;
	}

	// NEW
	@Override
	public int createPatientDoc(String emaildoc, String password, String name, String surname, String idapp) {
		Connection conn = getConnection();
		DSLContext database = DSL.using(conn, SQLDialect.MYSQL, new Settings().withExecuteLogging(true));

		SelectConditionStep<Record1<String>> searchuser = database.select(USER.EMAIL).from(USER)
				.where(USER.EMAIL.eq(emaildoc).and(USER.PASSWORD.eq(password)));
		SelectConditionStep<Record1<String>> searchapp = database.select(APPLICATION.ID).from(APPLICATION)
				.where(APPLICATION.ID.eq(idapp));

		if (searchuser.fetchOne() != null && searchapp.fetchOne() != null) {
			PatientdocRecord result = database
					.insertInto(PATIENTDOC, PATIENTDOC.SURNAME, PATIENTDOC.NAME, PATIENTDOC.EMAILDOC)
					.values(surname, name, emaildoc).returning(PATIENTDOC.ID).fetchOne();
			return Integer.valueOf(result.getId().toString());
		} else
			return 0;
	}

	// NEW
	@Override
	public int deletePatient(String emaildoc, String password, String idpatient, String idapp) {
		Connection conn = getConnection();
		DSLContext database = DSL.using(conn, SQLDialect.MYSQL, new Settings().withExecuteLogging(true));

		SelectConditionStep<Record1<String>> searchuser = database.select(USER.EMAIL).from(USER)
				.where(USER.EMAIL.eq(emaildoc).and(USER.PASSWORD.eq(password)));
		SelectConditionStep<Record1<UInteger>> searchpatient = database.select(PATIENTDOC.ID).from(PATIENTDOC)
				.where(PATIENTDOC.EMAILDOC.eq(emaildoc).and(PATIENTDOC.ID.eq(UInteger.valueOf(idpatient))));
		SelectConditionStep<Record1<String>> searchapp = database.select(APPLICATION.ID).from(APPLICATION)
				.where(APPLICATION.ID.eq(idapp));

		if ((searchuser.fetchOne() != null) && (searchpatient.fetchOne() != null) && (searchapp.fetchOne() != null)) {
			database.delete(RESULT_NOT_REGISTERED).where(RESULT_NOT_REGISTERED.IDUTENTE.eq(UInteger.valueOf(idpatient)))
					.and(RESULT_NOT_REGISTERED.IDAPP.eq(idapp)).execute();
			database.delete(PATIENTDOC).where(PATIENTDOC.ID.eq(UInteger.valueOf(idpatient)))
					.and(PATIENTDOC.EMAILDOC.eq(emaildoc)).execute();
			return 1;
		}
		return 0;
	}

	// NEW
	@Override
	public int storeResultsNotRegistered(String emaildoc, String password, String idpatient, String idapp,
			Timestamp datetime, String result) {
		Connection conn = getConnection();
		DSLContext database = DSL.using(conn, SQLDialect.MYSQL, new Settings().withExecuteLogging(true));

		SelectConditionStep<Record1<String>> searchuser = database.select(USER.EMAIL).from(USER)
				.where(USER.EMAIL.eq(emaildoc).and(USER.PASSWORD.eq(password)));
		SelectConditionStep<Record1<UInteger>> searchpatient = database.select(PATIENTDOC.ID).from(PATIENTDOC)
				.where(PATIENTDOC.EMAILDOC.eq(emaildoc).and(PATIENTDOC.ID.eq(UInteger.valueOf(idpatient))));
		SelectConditionStep<Record1<String>> searchapp = database.select(APPLICATION.ID).from(APPLICATION)
				.where(APPLICATION.ID.eq(idapp));

		// Append date and time to result
		result = "{"
				+ result.toString().substring(result.toString().indexOf("{") + 1, result.toString().lastIndexOf("}"))
				+ ",\"dateandtime\":\"" + datetime.toString() + "\"}";

		if ((searchuser.fetchOne() != null) && (searchpatient.fetchOne() != null) && (searchapp.fetchOne() != null)) {
			database.insertInto(RESULT_NOT_REGISTERED, RESULT_NOT_REGISTERED.IDAPP, RESULT_NOT_REGISTERED.DATEANDTIME,
					RESULTS.RESULT, RESULT_NOT_REGISTERED.IDUTENTE)
					.values(idapp, datetime, result, UInteger.valueOf(idpatient)).execute();
			return 1;
		} else
			return 0;
	}

	// NEW
	@Override
	public ArrayList<String> getResultsNotRegistered(String emaildoc, String password, String idpatient, String idapp) {
		Connection conn = getConnection();
		DSLContext database = DSL.using(conn, SQLDialect.MYSQL, new Settings().withExecuteLogging(true));

		SelectConditionStep<Record1<String>> searchuser = database.select(USER.EMAIL).from(USER)
				.where(USER.EMAIL.eq(emaildoc).and(USER.PASSWORD.eq(password)));
		SelectConditionStep<Record1<UInteger>> searchpatient = database.select(PATIENTDOC.ID).from(PATIENTDOC)
				.where(PATIENTDOC.EMAILDOC.eq(emaildoc).and(PATIENTDOC.ID.eq(UInteger.valueOf(idpatient))));
		SelectConditionStep<Record1<String>> searchapp = database.select(APPLICATION.ID).from(APPLICATION)
				.where(APPLICATION.ID.eq(idapp));

		SelectConditionStep<Record1<String>> result = null;
		ArrayList<String> list = new ArrayList<>();

		if ((searchuser.fetchOne() != null) && (searchpatient.fetchOne() != null) && (searchapp.fetchOne() != null)) {
			result = database.select(RESULT_NOT_REGISTERED.RESULT.as("result")).from(RESULT_NOT_REGISTERED)
					.where(RESULT_NOT_REGISTERED.IDUTENTE.eq(UInteger.valueOf(idpatient))
							.and(RESULT_NOT_REGISTERED.IDAPP.eq(idapp)));
			for (Record1<String> single : result)
				list.add((String) single.get("result"));

			return list;
		} else
			return null;
	}

	// NEW
	@Override
	public int deleteResultsAll(String emaildoc, String password, String idpatient, String idapp) {
		Connection conn = getConnection();
		DSLContext database = DSL.using(conn, SQLDialect.MYSQL, new Settings().withExecuteLogging(true));

		SelectConditionStep<Record1<String>> searchuser = database.select(USER.EMAIL).from(USER)
				.where(USER.EMAIL.eq(emaildoc).and(USER.PASSWORD.eq(password)));
		SelectConditionStep<Record1<UInteger>> searchpatient = database.select(PATIENTDOC.ID).from(PATIENTDOC)
				.where(PATIENTDOC.EMAILDOC.eq(emaildoc).and(PATIENTDOC.ID.eq(UInteger.valueOf(idpatient))));
		SelectConditionStep<Record1<String>> searchapp = database.select(APPLICATION.ID).from(APPLICATION)
				.where(APPLICATION.ID.eq(idapp));

		if ((searchuser.fetchOne() != null) && (searchpatient.fetchOne() != null) && (searchapp.fetchOne() != null)) {
			database.delete(RESULT_NOT_REGISTERED).where(RESULT_NOT_REGISTERED.IDUTENTE.eq(UInteger.valueOf(idpatient)))
					.and(RESULT_NOT_REGISTERED.IDAPP.eq(idapp)).execute();
			return 1;
		}
		return 0;
	}

	// NEW
	@Override
	public int deleteResultSingle(String emaildoc, String password, String idpatient, String idapp, String result) {
		Connection conn = getConnection();
		DSLContext database = DSL.using(conn, SQLDialect.MYSQL, new Settings().withExecuteLogging(true));

		SelectConditionStep<Record1<String>> searchuser = database.select(USER.EMAIL).from(USER)
				.where(USER.EMAIL.eq(emaildoc).and(USER.PASSWORD.eq(password)));
		SelectConditionStep<Record1<UInteger>> searchpatient = database.select(PATIENTDOC.ID).from(PATIENTDOC)
				.where(PATIENTDOC.EMAILDOC.eq(emaildoc).and(PATIENTDOC.ID.eq(UInteger.valueOf(idpatient))));
		SelectConditionStep<Record1<String>> searchapp = database.select(APPLICATION.ID).from(APPLICATION)
				.where(APPLICATION.ID.eq(idapp));

		if ((searchuser.fetchOne() != null) && (searchpatient.fetchOne() != null) && (searchapp.fetchOne() != null)) {
			database.delete(RESULT_NOT_REGISTERED).where(RESULT_NOT_REGISTERED.IDUTENTE.eq(UInteger.valueOf(idpatient)))
					.and(RESULT_NOT_REGISTERED.IDAPP.eq(idapp)).and(RESULT_NOT_REGISTERED.RESULT.eq(result)).execute();
			return 1;
		}
		return 0;
	}
}