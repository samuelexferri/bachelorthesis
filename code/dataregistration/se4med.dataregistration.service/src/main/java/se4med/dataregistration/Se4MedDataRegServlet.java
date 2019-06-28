package se4med.dataregistration;

import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Se4MedService
 */
@SuppressWarnings("serial")
@WebServlet("/")
public class Se4MedDataRegServlet extends HttpServlet {
	
	/**
	 * PARAMETER FOR SAVING RESULTS
	 */
	public static final String USER_PARAM = "username";
	public static final String PASSWORD_PARAM = "password";
	public static final String EMAIL_PARAM = "useremail";
	public static final String IDAPP_PARAM = "idapp";
	public static final String DATETIME_PARAM = "dateandtime";
	public static final String RESULT_PARAM = "result";
	public static final String APPSETTINGS_PARAM = "appsettings";

	// NEW
	public static final String NAMEPAT_PARAM = "namepat";
	public static final String SURNAMEPAT_PARAM = "surnamepat";
	public static final String IDPATIENT_PARAM = "idpatient";

	/**
	 * OLD SERVICE PROVIDED BY THE SERVLET
	 */
	// Action requested by the client
	public static final String ACTION_PARAM_NAME = "action";
	// Initial request to authenticate the patient
	public static final String AUTHENTICATE_PATIENT_ACTION = "authenticatepatient";
	// Initial request to authenticate the patient
	public static final String AUTHENTICATE_ACTION = "authenticate";
	// Initial request to authenticate the doctor
	public static final String AUTHENTICATE_DOCTOR_ACTION = "authenticatedoctor";
	// Request to store application results
	public static final String STORERESULTS_ACTION = "storeresults";
	// Request to store application results
	public static final String STORERESULTS_NOPW_ACTION = "storeresultsnopw";
	// Request to store application settings
	public static final String STORESETTINGS_ACTION = "storesettings";
	// Request to get application settings
	public static final String GETSETTINGS_ACTION = "getsettings";

	/**
	 * NEW SERVICE PROVIDED BY THE SERVLET
	 */
	// NEW: Authenticate doctor using JSON (login, name, surname)
	public static final String AUTHENTICATE_DOCTOR_NS_ACTION = "authenticatedoctorns";
	// NEW: Request to get patient list registered by the doctor
	public static final String GETPATIENTDOCLIST_ACTION = "getpatientdoclist";
	// NEW: Request to set patient registered by the doctor
	public static final String CREATEPATIENTDOC_ACTION = "createpatientdoc";
	// NEW: Request to delete patient registered by the doctor
	public static final String DELETEPATIENTDOC_ACTION = "deletepatientdoc";
	// NEW: Request to store application results for user registered by the doctor
	// [STORE SINGLE TEST RESULT OF A PATIENT]
	public static final String STORERESULTS_NOT_REGISTERED_ACTION = "storeresultsnotregistered";
	// NEW: Request to get application results for user registered by the doctor
	// [GET ALL TEST RESULTS OF A PATIENT]
	public static final String GETRESULTS_NOT_REGISTERED_ACTION = "getresultsnotregistered";
	// NEW: Request to delete application results for user registered by the doctor
	// [DELETE ALL TEST RESULTS OF A PATIENT]
	public static final String DELETERESULTS_ALL_ACTION = "deleteresultsall";
	// NEW: Request to delete application results for user registered by the doctor
	// [DELETE SINGLE TEST RESULT OF A PATIENT]
	public static final String DELETERESULT_SINGLE_ACTION = "deleteresultsingle";

	/**
	 * PARAMETERS
	 */
	// Response in case of authentication OK
	public static final String LOGIN_OK = "login_ok";
	// Parameter name for the data *JSON"
	public static final String DATA_PARAM_NAME = "data";
	// Parameter name for time stamp
	public static final String TIME_STAMP = "timestamp";

	// NEW: Parameter status OK
	public static final String STATUS_OK = "status_ok";

	Se4MedDataConnector dc;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	Se4MedDataRegServlet(Se4MedDataConnector dc) {
		super();
		this.dc = dc;
	}

	public Se4MedDataRegServlet() {
		this(DBSe4MedDataConnector.instance);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
		String action = request.getParameter(ACTION_PARAM_NAME);
		if (action == null) {
			response.getWriter().append("no_specified_action");
			return;
		}
		switch (action) {
		case AUTHENTICATE_ACTION:
			authenticate(request, response);
			break;
		case AUTHENTICATE_PATIENT_ACTION:
			authenticatePatient(request, response);
			break;
		case AUTHENTICATE_DOCTOR_ACTION:
			authenticateDoctor(request, response);
			break;
		case STORERESULTS_ACTION:
			storeResults(request, response);
			break;
		case STORERESULTS_NOPW_ACTION:
			storeResultsNoPw(request, response);
			break;
		case STORESETTINGS_ACTION:
			storeSettings(request, response);
			break;
		case GETSETTINGS_ACTION:
			getSettings(request, response);
			break;
		// NEW
		case AUTHENTICATE_DOCTOR_NS_ACTION:
			authenticateDoctorNameSurname(request, response);
			break;
		// NEW
		case GETPATIENTDOCLIST_ACTION:
			getPatientDocList(request, response);
			break;
		// NEW
		case CREATEPATIENTDOC_ACTION:
			createPatientDoc(request, response);
			break;
		// NEW
		case DELETEPATIENTDOC_ACTION:
			deletePatient(request, response);
			break;
		// NEW
		case STORERESULTS_NOT_REGISTERED_ACTION:
			storeResultsNotRegistered(request, response);
			break;
		// NEW
		case GETRESULTS_NOT_REGISTERED_ACTION:
			getResultsNotRegistered(request, response);
			break;
		// NEW
		case DELETERESULTS_ALL_ACTION:
			deleteResultsAll(request, response);
			break;
		// NEW
		case DELETERESULT_SINGLE_ACTION:
			deleteResultSingle(request, response);
			break;
		default:
			response.getWriter().append("action_unkown");
			break;
		}
	}

	private void authenticate(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String emailuser = URLDecoder.decode(request.getParameter(EMAIL_PARAM), "UTF-8");
		String password = URLDecoder.decode(request.getParameter(PASSWORD_PARAM), "UTF-8");
		Map<String, String> messages = new HashMap<String, String>();
		int find_user = dc.findUser(emailuser, password);
		if (find_user == 1) {
			messages.put("login", "LOGIN OK");
			response.getWriter().append(messages.toString());
			return;
		} else {
			messages.put("login", "Unknown login, please try again");
		}
		response.getWriter().append(messages.toString());

	}

	private void getSettings(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String username = URLDecoder.decode(request.getParameter(USER_PARAM), "UTF-8");
		String emailuser = URLDecoder.decode(request.getParameter(EMAIL_PARAM), "UTF-8");
		String idapp = URLDecoder.decode(request.getParameter(IDAPP_PARAM), "UTF-8");
		String outcome = dc.getAppSettings(username, emailuser, idapp);
		if (outcome.equalsIgnoreCase(""))
			response.getWriter().append("No settings found");
		else
			response.getWriter().append(outcome);
	}

	private void storeSettings(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String username = URLDecoder.decode(request.getParameter(USER_PARAM), "UTF-8");
		String emailuser = URLDecoder.decode(request.getParameter(EMAIL_PARAM), "UTF-8");
		String idapp = URLDecoder.decode(request.getParameter(IDAPP_PARAM), "UTF-8");
		String appsettings = URLDecoder.decode(request.getParameter(APPSETTINGS_PARAM), "UTF-8");
		int outcome = dc.saveAppSettings(username, emailuser, idapp, appsettings);
		if (outcome == 1)
			response.getWriter().append("Settings saved");
		else
			response.getWriter().append("Error in settings saving");
	}

	private void storeResults(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String username = URLDecoder.decode(request.getParameter(USER_PARAM), "UTF-8");
		String emailuser = URLDecoder.decode(request.getParameter(EMAIL_PARAM), "UTF-8");
		String password = URLDecoder.decode(request.getParameter(PASSWORD_PARAM), "UTF-8");
		String idapp = URLDecoder.decode(request.getParameter(IDAPP_PARAM), "UTF-8");
		Timestamp dateTime = new Timestamp(System.currentTimeMillis());
		String result = URLDecoder.decode(request.getParameter(RESULT_PARAM), "UTF-8");
		System.out.println(result);
		int outcome = dc.saveResults(username, emailuser, password, idapp, dateTime, result);
		if (outcome == 1)
			response.getWriter().append("Data saved");
		else if (outcome == 0)
			response.getWriter().append("Error in data saving");
		else
			response.getWriter().append("Error in user account " + emailuser + password);
	}

	private void storeResultsNoPw(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String username = URLDecoder.decode(request.getParameter(USER_PARAM), "UTF-8");
		String emailuser = URLDecoder.decode(request.getParameter(EMAIL_PARAM), "UTF-8");
		String idapp = URLDecoder.decode(request.getParameter(IDAPP_PARAM), "UTF-8");
		Timestamp dateTime = new Timestamp(System.currentTimeMillis());
		String result = URLDecoder.decode(request.getParameter(RESULT_PARAM), "UTF-8");
		System.out.println(result);
		int outcome = dc.saveResults(username, emailuser, idapp, dateTime, result);
		if (outcome == 1)
			response.getWriter().append("Data saved");
		else
			response.getWriter().append("Error in data saving");
	}

	private void authenticateDoctor(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String emailuser = URLDecoder.decode(request.getParameter(EMAIL_PARAM), "UTF-8");
		String password = URLDecoder.decode(request.getParameter(PASSWORD_PARAM), "UTF-8");
		Map<String, String> messages = new HashMap<String, String>();
		int find_user = dc.findDoctor(emailuser, password);
		if (find_user == 1) {
			messages.put("login", "LOGIN OK");
			response.getWriter().append(messages.toString());
			return;
		} else {
			messages.put("login", "Unknown login, please try again");
		}
		response.getWriter().append(messages.toString());
	}

	private void authenticatePatient(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String emailuser = URLDecoder.decode(request.getParameter(EMAIL_PARAM), "UTF-8");
		String password = URLDecoder.decode(request.getParameter(PASSWORD_PARAM), "UTF-8");
		String idapp = URLDecoder.decode(request.getParameter(IDAPP_PARAM), "UTF-8");
		ArrayList<String> userlist = dc.findPatient(emailuser, idapp, password);
		Map<String, String> messages = new HashMap<String, String>();
		if (userlist.size() != 0) {
			messages.put("authenticationPatient", convertString(userlist));
			response.getWriter().append(messages.toString());
			return;
		} else {
			messages.put("authenticationPatient", "Authentication failed. Wrong credential or no user found.");
		}
		response.getWriter().append(messages.toString());
	}

	private String convertString(ArrayList<String> userlist) {
		String users = "";
		for (int i = 0; i < userlist.size(); i++) {
			users += userlist.get(i) + ",";
		}
		return users.substring(0, users.length() - 1);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	/**
	 * NEW SERVICE PROVIDED
	 */

	// NEW: From ArrayList to JSON Object Array
	private String converJSON(ArrayList<String> list) {
		String x = "[" + list.get(0);
		for (int i = 1; i < list.size(); i++) {
			x += "," + list.get(i);
		}
		x += "]";
		return x;
	}

	// NEW: Authenticate doctor using JSON (login, name, surname)
	private void authenticateDoctorNameSurname(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String emaildoc = URLDecoder.decode(request.getParameter(EMAIL_PARAM), "UTF-8");
		String password = URLDecoder.decode(request.getParameter(PASSWORD_PARAM), "UTF-8");
		String idapp = URLDecoder.decode(request.getParameter(IDAPP_PARAM), "UTF-8");

		int find_user = dc.findDoctor(emaildoc, password);

		if (find_user == 1) {
			String ns = dc.getNameSurnameDoctor(emaildoc, password, idapp);
			response.getWriter().append("{login:" + LOGIN_OK + "," + ns + "}");
		} else {
			response.getWriter().append("{login:unknown_login_please_try_again}");
		}
		return;
	}

	// NEW: Request to get patient list registered by the doctor
	private void getPatientDocList(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String emaildoc = URLDecoder.decode(request.getParameter(EMAIL_PARAM), "UTF-8");
		String password = URLDecoder.decode(request.getParameter(PASSWORD_PARAM), "UTF-8");
		String idapp = URLDecoder.decode(request.getParameter(IDAPP_PARAM), "UTF-8");

		ArrayList<String> outcome = dc.getPatientDocList(emaildoc, password, idapp);
		String out = null;

		if (outcome.isEmpty()) {
			response.getWriter().append("{status:no_patients_found}");
		} else {
			out = converJSON(outcome);
			response.getWriter().append("{status:" + STATUS_OK + ",patients:" + out + "}");
		}
	}

	// NEW: Request to set patient registered by the doctor
	private void createPatientDoc(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String emaildoc = URLDecoder.decode(request.getParameter(EMAIL_PARAM), "UTF-8");
		String password = URLDecoder.decode(request.getParameter(PASSWORD_PARAM), "UTF-8");
		String name = URLDecoder.decode(request.getParameter(NAMEPAT_PARAM), "UTF-8");
		String surname = URLDecoder.decode(request.getParameter(SURNAMEPAT_PARAM), "UTF-8");
		String idapp = URLDecoder.decode(request.getParameter(IDAPP_PARAM), "UTF-8");

		int outcome = dc.createPatientDoc(emaildoc, password, name, surname, idapp);

		if (outcome != 0)
			response.getWriter().append("{status:" + STATUS_OK + ",idpatient:" + outcome + "}");
		else
			response.getWriter().append("{status:no_patient_created}");
	}

	// NEW: Request to delete patient registered by the doctor
	private void deletePatient(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String emaildoc = URLDecoder.decode(request.getParameter(EMAIL_PARAM), "UTF-8");
		String password = URLDecoder.decode(request.getParameter(PASSWORD_PARAM), "UTF-8");
		String idpatient = URLDecoder.decode(request.getParameter(IDPATIENT_PARAM), "UTF-8");
		String idapp = URLDecoder.decode(request.getParameter(IDAPP_PARAM), "UTF-8");

		int outcome = dc.deletePatient(emaildoc, password, idpatient, idapp);

		if (outcome == 0) {
			response.getWriter().append("{status:no_patient_deleted}");
		} else {
			response.getWriter().append("{status:" + STATUS_OK + "}");
		}
	}

	// NEW: Request to store application results for user registered by the doctor
	// [STORE SINGLE TEST RESULT OF A PATIENT]
	private void storeResultsNotRegistered(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		String emaildoc = URLDecoder.decode(request.getParameter(EMAIL_PARAM), "UTF-8");
		String password = URLDecoder.decode(request.getParameter(PASSWORD_PARAM), "UTF-8");
		String idpatient = URLDecoder.decode(request.getParameter(IDPATIENT_PARAM), "UTF-8");
		String idapp = URLDecoder.decode(request.getParameter(IDAPP_PARAM), "UTF-8");
		Timestamp datetime = new Timestamp(System.currentTimeMillis());
		String result = URLDecoder.decode(request.getParameter(RESULT_PARAM), "UTF-8");
		System.out.println(result);

		int outcome = dc.storeResultsNotRegistered(emaildoc, password, idpatient, idapp, datetime, result);

		if (outcome == 1)
			response.getWriter().append("{status:" + STATUS_OK + "}");
		else if (outcome == 0)
			response.getWriter().append("{status:error_in_data_saving}");
	}

	// NEW: Request to get application results for user registered by the doctor
	// [GET ALL TEST RESULTS OF A PATIENT]
	private void getResultsNotRegistered(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String emaildoc = URLDecoder.decode(request.getParameter(EMAIL_PARAM), "UTF-8");
		String password = URLDecoder.decode(request.getParameter(PASSWORD_PARAM), "UTF-8");
		String idpatient = URLDecoder.decode(request.getParameter(IDPATIENT_PARAM), "UTF-8");
		String idapp = URLDecoder.decode(request.getParameter(IDAPP_PARAM), "UTF-8");

		ArrayList<String> outcome = dc.getResultsNotRegistered(emaildoc, password, idpatient, idapp);
		String out = null;

		if (outcome.isEmpty()) {
			response.getWriter().append("{status:no_results_found}");
		} else {
			out = converJSON(outcome);
			response.getWriter().append("{status:" + STATUS_OK + ",results:" + out + "}");
		}
	}

	// NEW: Request to delete application results for user registered by the doctor
	// [DELETE ALL TEST RESULT OF A PATIENT]
	private void deleteResultsAll(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String emaildoc = URLDecoder.decode(request.getParameter(EMAIL_PARAM), "UTF-8");
		String password = URLDecoder.decode(request.getParameter(PASSWORD_PARAM), "UTF-8");
		String idpatient = URLDecoder.decode(request.getParameter(IDPATIENT_PARAM), "UTF-8");
		String idapp = URLDecoder.decode(request.getParameter(IDAPP_PARAM), "UTF-8");

		int outcome = dc.deleteResultsAll(emaildoc, password, idpatient, idapp);

		if (outcome == 0) {
			response.getWriter().append("{status:no_results_deleted}");
		} else {
			response.getWriter().append("{status:" + STATUS_OK + "}");
		}
	}

	// NEW: Request to delete application results for user registered by the doctor
	// [DELETE SINGLE TEST RESULT OF A PATIENT]
	private void deleteResultSingle(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String emaildoc = URLDecoder.decode(request.getParameter(EMAIL_PARAM), "UTF-8");
		String password = URLDecoder.decode(request.getParameter(PASSWORD_PARAM), "UTF-8");
		String idpatient = URLDecoder.decode(request.getParameter(IDPATIENT_PARAM), "UTF-8");
		String idapp = URLDecoder.decode(request.getParameter(IDAPP_PARAM), "UTF-8");
		String result = URLDecoder.decode(request.getParameter(RESULT_PARAM), "UTF-8");

		int outcome = dc.deleteResultSingle(emaildoc, password, idpatient, idapp, result);

		if (outcome == 0) {
			response.getWriter().append("{status:no_result_deleted}");
		} else {
			response.getWriter().append("{status:" + STATUS_OK + "}");
		}
	}
}