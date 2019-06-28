package se4med.dataregistration;

import java.sql.Timestamp;
import java.util.ArrayList;

public interface Se4MedDataConnector {

	/**
	 *  OLD SERVICES
	 */
	
	int saveResults(String username, String emailuser, String idApp, Timestamp dateTime, String result);

	int saveResults(String username, String emailuser, String password, String idApp, Timestamp dateTime,
			String result);

	int saveAppSettings(String username, String emailuser, String idapp, String appsettings);

	int findDoctor(String username, String password);

	ArrayList<String> findPatient(String email, String idapp, String password);

	String getAppSettings(String username, String emailuser, String idapp);

	int findUser(String emailuser, String password);

	/**
	 *  NEW SERVICES
	 */
	
	// NEW
	String getNameSurnameDoctor(String emaildoc, String password, String idapp);

	// NEW
	ArrayList<String> getPatientDocList(String emaildoc, String password, String idapp);

	// NEW
	int createPatientDoc(String emaildoc, String password, String name, String surname, String idapp);

	// NEW
	int deletePatient(String emaildoc, String password, String idpatient, String idapp);

	// NEW
	int storeResultsNotRegistered(String emaildoc, String password, String idpatient, String idapp, Timestamp datetime,
			String result);

	// NEW
	ArrayList<String> getResultsNotRegistered(String emaildoc, String password, String idpatient, String idapp);

	// NEW
	int deleteResultsAll(String emaildoc, String password, String idpatient, String idapp);

	// NEW
	int deleteResultSingle(String emaildoc, String password, String idpatient, String idapp, String results);
}