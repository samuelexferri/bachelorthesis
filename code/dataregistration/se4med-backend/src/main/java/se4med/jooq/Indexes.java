/*
 * This file is generated by jOOQ.
 */
package se4med.jooq;


import javax.annotation.Generated;

import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.Internal;

import se4med.jooq.tables.Application;
import se4med.jooq.tables.Doctor;
import se4med.jooq.tables.Doctorapp;
import se4med.jooq.tables.Doctorpatient;
import se4med.jooq.tables.Patient;
import se4med.jooq.tables.Patientdoc;
import se4med.jooq.tables.Project;
import se4med.jooq.tables.ResultNotRegistered;
import se4med.jooq.tables.Results;
import se4med.jooq.tables.User;


/**
 * A class modelling indexes of tables of the <code>se4med</code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.2"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Indexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index APPLICATION_IDPROJECT_IDX = Indexes0.APPLICATION_IDPROJECT_IDX;
    public static final Index APPLICATION_PRIMARY = Indexes0.APPLICATION_PRIMARY;
    public static final Index DOCTOR_PRIMARY = Indexes0.DOCTOR_PRIMARY;
    public static final Index DOCTORAPP_DOCTORAPP_IDX = Indexes0.DOCTORAPP_DOCTORAPP_IDX;
    public static final Index DOCTORAPP_PRIMARY = Indexes0.DOCTORAPP_PRIMARY;
    public static final Index DOCTORPATIENT_APPUSER_IDX = Indexes0.DOCTORPATIENT_APPUSER_IDX;
    public static final Index DOCTORPATIENT_EMAILUSER_IDX = Indexes0.DOCTORPATIENT_EMAILUSER_IDX;
    public static final Index DOCTORPATIENT_PRIMARY = Indexes0.DOCTORPATIENT_PRIMARY;
    public static final Index PATIENT_EMAILUSER = Indexes0.PATIENT_EMAILUSER;
    public static final Index PATIENT_PRIMARY = Indexes0.PATIENT_PRIMARY;
    public static final Index PATIENTDOC_FK_PATIENTDOC_1_IDX = Indexes0.PATIENTDOC_FK_PATIENTDOC_1_IDX;
    public static final Index PATIENTDOC_PRIMARY = Indexes0.PATIENTDOC_PRIMARY;
    public static final Index PROJECT_ID_UNIQUE = Indexes0.PROJECT_ID_UNIQUE;
    public static final Index PROJECT_PRIMARY = Indexes0.PROJECT_PRIMARY;
    public static final Index RESULT_NOT_REGISTERED_FK_RESULT_NOT_REGISTERED_1_IDX = Indexes0.RESULT_NOT_REGISTERED_FK_RESULT_NOT_REGISTERED_1_IDX;
    public static final Index RESULT_NOT_REGISTERED_PRIMARY = Indexes0.RESULT_NOT_REGISTERED_PRIMARY;
    public static final Index RESULTS_APPRESULTS_IDX = Indexes0.RESULTS_APPRESULTS_IDX;
    public static final Index RESULTS_PRIMARY = Indexes0.RESULTS_PRIMARY;
    public static final Index RESULTS_USERNAME = Indexes0.RESULTS_USERNAME;
    public static final Index USER_PRIMARY = Indexes0.USER_PRIMARY;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Indexes0 {
        public static Index APPLICATION_IDPROJECT_IDX = Internal.createIndex("idproject_idx", Application.APPLICATION, new OrderField[] { Application.APPLICATION.IDPROJECT }, false);
        public static Index APPLICATION_PRIMARY = Internal.createIndex("PRIMARY", Application.APPLICATION, new OrderField[] { Application.APPLICATION.ID }, true);
        public static Index DOCTOR_PRIMARY = Internal.createIndex("PRIMARY", Doctor.DOCTOR, new OrderField[] { Doctor.DOCTOR.EMAIL }, true);
        public static Index DOCTORAPP_DOCTORAPP_IDX = Internal.createIndex("doctorapp_idx", Doctorapp.DOCTORAPP, new OrderField[] { Doctorapp.DOCTORAPP.IDAPP }, false);
        public static Index DOCTORAPP_PRIMARY = Internal.createIndex("PRIMARY", Doctorapp.DOCTORAPP, new OrderField[] { Doctorapp.DOCTORAPP.EMAILDOCTOR, Doctorapp.DOCTORAPP.IDAPP }, true);
        public static Index DOCTORPATIENT_APPUSER_IDX = Internal.createIndex("appuser_idx", Doctorpatient.DOCTORPATIENT, new OrderField[] { Doctorpatient.DOCTORPATIENT.IDAPP }, false);
        public static Index DOCTORPATIENT_EMAILUSER_IDX = Internal.createIndex("emailUser_idx", Doctorpatient.DOCTORPATIENT, new OrderField[] { Doctorpatient.DOCTORPATIENT.USERNAME, Doctorpatient.DOCTORPATIENT.EMAILPATIENT }, false);
        public static Index DOCTORPATIENT_PRIMARY = Internal.createIndex("PRIMARY", Doctorpatient.DOCTORPATIENT, new OrderField[] { Doctorpatient.DOCTORPATIENT.EMAILDOCTOR, Doctorpatient.DOCTORPATIENT.EMAILPATIENT, Doctorpatient.DOCTORPATIENT.USERNAME, Doctorpatient.DOCTORPATIENT.IDAPP }, true);
        public static Index PATIENT_EMAILUSER = Internal.createIndex("emailuser", Patient.PATIENT, new OrderField[] { Patient.PATIENT.EMAILPATIENT }, false);
        public static Index PATIENT_PRIMARY = Internal.createIndex("PRIMARY", Patient.PATIENT, new OrderField[] { Patient.PATIENT.USERNAME, Patient.PATIENT.EMAILPATIENT }, true);
        public static Index PATIENTDOC_FK_PATIENTDOC_1_IDX = Internal.createIndex("fk_patientdoc_1_idx", Patientdoc.PATIENTDOC, new OrderField[] { Patientdoc.PATIENTDOC.EMAILDOC }, false);
        public static Index PATIENTDOC_PRIMARY = Internal.createIndex("PRIMARY", Patientdoc.PATIENTDOC, new OrderField[] { Patientdoc.PATIENTDOC.ID, Patientdoc.PATIENTDOC.EMAILDOC }, true);
        public static Index PROJECT_ID_UNIQUE = Internal.createIndex("id_UNIQUE", Project.PROJECT, new OrderField[] { Project.PROJECT.ID }, true);
        public static Index PROJECT_PRIMARY = Internal.createIndex("PRIMARY", Project.PROJECT, new OrderField[] { Project.PROJECT.ID }, true);
        public static Index RESULT_NOT_REGISTERED_FK_RESULT_NOT_REGISTERED_1_IDX = Internal.createIndex("fk_result_not_registered_1_idx", ResultNotRegistered.RESULT_NOT_REGISTERED, new OrderField[] { ResultNotRegistered.RESULT_NOT_REGISTERED.IDUTENTE }, false);
        public static Index RESULT_NOT_REGISTERED_PRIMARY = Internal.createIndex("PRIMARY", ResultNotRegistered.RESULT_NOT_REGISTERED, new OrderField[] { ResultNotRegistered.RESULT_NOT_REGISTERED.ID }, true);
        public static Index RESULTS_APPRESULTS_IDX = Internal.createIndex("appresults_idx", Results.RESULTS, new OrderField[] { Results.RESULTS.IDAPP }, false);
        public static Index RESULTS_PRIMARY = Internal.createIndex("PRIMARY", Results.RESULTS, new OrderField[] { Results.RESULTS.ID }, true);
        public static Index RESULTS_USERNAME = Internal.createIndex("username", Results.RESULTS, new OrderField[] { Results.RESULTS.USERNAME, Results.RESULTS.EMAILPATIENT }, false);
        public static Index USER_PRIMARY = Internal.createIndex("PRIMARY", User.USER, new OrderField[] { User.USER.EMAIL }, true);
    }
}
