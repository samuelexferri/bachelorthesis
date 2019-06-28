package com.unibg.app3dsat.util;

import android.support.annotation.NonNull;

import p3d4amb.sat.lib.shapes.ImageShape;
import p3d4amb.sat.lib.shapes.ShapeSize;

/**
 * Class: DefaultValues
 */
public class DefaultValues {

    /**
     * SPDOCTOR
     */
    public static final String SPDOCTOR = "SPDoctor";
    public static final String ACTUAL_DOCTOR_MAIL = "actual_doctor_mail";
    public static final String ACTUAL_DOCTOR_PASSWORD = "actual_doctor_password";
    public static final String ACTUAL_DOCTOR_NAME = "actual_doctor_name";
    public static final String ACTUAL_DOCTOR_SURNAME = "actual_doctor_surname";

    /**
     * SPPATIENT
     */
    public static final String SPPATIENT = "SPPatient";
    public static final String ACTUAL_PATIENT_NAME = "actual_patient_name";
    public static final String ACTUAL_PATIENT_SURNAME = "actual_patient_surname";
    public static final String ACTUAL_PATIENT_ID = "actual_patient_id";

    /**
     * SPSETTINGS
     */
    public static final String SPSETTINGS = "SPSettings";
    public static final String PREF_DISTANCE = "pref_distance";
    public static final String PREF_MAXDISPARITY = "pref_maxdisparity";
    public static final String PREF_MINDISPARITY = "pref_mindisparity";
    public static final String PREF_IMAGESET = "pref_imageset";
    public static final String PREF_OFFSET = "pref_offset";
    public static final String PREF_NCORR_TO_NEXTLEVEL = "pref_ncorrtonextlevel";
    public static final String PREF_NERR_TOSTOPTEST = "pref_nerrtostoptest";
    public static final int DEFAULT_DISTANCE = 30;
    public static final int DEFAULT_MAXDISPARITY = 12;
    public static final int DEFAULT_OFFSET = 1;
    public static final int DEFAULT_NCORR_TONEXTLEVE = 1;
    public static final int DEFAULT_NERR_TOSTOPTEST = 3;
    public static final ImageShape.ImageSet DEFAULT_IMAGESET = ImageShape.ImageSet.LANG;
    public static final int POSSIBLECHOICES = 4; // Possible choices in Test

    /**
     * SetColors default values
     */
    public static final String RED_L = "red_l";
    public static final String RED_R = "red_r";
    public static final String GREEN_L = "green_l";
    public static final String GREEN_R = "green_r";
    public static final String BLUE_L = "blue_l";
    public static final String BLUE_R = "blue_r";
    public static final int CURRENT_RED_L = 255;
    public static final int CURRENT_RED_R = 0;
    public static final int CURRENT_GREEN_L = 0;
    public static final int CURRENT_GREEN_R = 0;
    public static final int CURRENT_BLUE_L = 0;
    public static final int CURRENT_BLUE_R = 255;

    /**
     * Other values
     */
    public static final String WIDTH_PIX = "widthPix";
    public static final String HEIGHT_PIX = "heighPix";
    public static final String WIDTH_PIX_PER_INCH = "widthPixPerInch";
    public static final String HEIGHT_PIX_PER_INCH = "heighPixPerInch";
    public static final String SESSION_DATA = "sessiondata";
    public static final String CURRENT_IMAGE_SET = "imageset";
    public static final String FINALANGLERESULT = "finalangleresult";
    public static final String FOLDER_RESULTS = "3DSAT/Results";
    public static final String FILE_RESULTS_NAME = "results";

    /**
     * Servlet
     */
    public static final String AUTHORITY = "se4med.unibg.it"; // Local: 192.168.137.1:9997
    public static final String STEREOTEST = "StereoTest";

    public static final String LOGIN_OK = "login_ok";
    public static final String STATUS_OK = "status_ok";

    public static final String ACTION_PARAM_NAME = "action";
    public static final String EMAIL_PARAM = "useremail";
    public static final String IDAPP_PARAM = "idapp";
    public static final String IDPATIENT_PARAM = "idpatient";
    public static final String NAMEPAT_PARAM = "namepat";
    public static final String PASSWORD_PARAM = "password";
    public static final String RESULT_PARAM = "result";
    public static final String SURNAMEPAT_PARAM = "surnamepat";
    public static final String DATEANDTIME_PARAM = "dateandtime";

    public static final String AUTHENTICATE_DOCTOR_NS_ACTION = "authenticatedoctorns";
    public static final String GETPATIENTDOCLIST_ACTION = "getpatientdoclist";
    public static final String CREATEPATIENTDOC_ACTION = "createpatientdoc";
    public static final String DELETEPATIENTDOC_ACTION = "deletepatientdoc";
    public static final String STORERESULTS_NOT_REGISTERED_ACTION = "storeresultsnotregistered";
    public static final String GETRESULTS_NOT_REGISTERED_ACTION = "getresultsnotregistered";
    public static final String DELETERESULTS_ALL_ACTION = "deleteresultsall";
    public static final String DELETERESULT_SINGLE_ACTION = "deleteresultsingle";

    /**
     * Method: stringToImageSet
     * Static util method that link the string name of an ImageSet to the given ImageSet
     *
     * @param name ImageSet
     * @return ImageSet object
     */
    @NonNull
    public static ImageShape.ImageSet stringToImageSet(String name) {
        if (name.equals("LANG"))
            return ImageShape.ImageSet.LANG;
        if (name.equals("LEA"))
            return ImageShape.ImageSet.LEA;
        if (name.equals("LEA_CONTORNO"))
            return ImageShape.ImageSet.LEA_CONTORNO;
        if (name.equals("LETTERS"))
            return ImageShape.ImageSet.LETTERS;
        if (name.equals("PACMAN"))
            return ImageShape.ImageSet.PACMAN;
        if (name.equals("TNO"))
            return ImageShape.ImageSet.TNO;
        return ImageShape.ImageSet.LANG; // Default value
    }

    /**
     * Method: getSize
     * Static util method that choose the right ShapeSize to fit the screen
     *
     * @param monitorSize10thInc (Dimensions of the diagonal of the screen in inch * 10)
     * @return ShapeSize (SMALL, MEDIUM, BIG)
     */
    @NonNull
    public static ShapeSize getSize(double monitorSize10thInc) {
        if (monitorSize10thInc <= 45) return ShapeSize.SMALL;
        else if (monitorSize10thInc <= 60) return ShapeSize.MEDIUM;
        else
            return ShapeSize.BIG;
    }
}