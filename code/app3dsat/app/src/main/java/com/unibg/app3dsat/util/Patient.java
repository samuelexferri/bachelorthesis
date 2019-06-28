package com.unibg.app3dsat.util;


import android.support.annotation.NonNull;

/**
 * Class: Patient
 */
public class Patient implements Comparable<Patient> {

    private final String name;
    private final String surname;
    private final int id;

    /**
     * Constructor: Patient
     *
     * @param name
     * @param surname
     * @param id
     */
    public Patient(String name, String surname, int id) {
        this.name = name;
        this.surname = surname;
        this.id = id;
    }

    /**
     * Method: getName
     *
     * @return String
     */
    public String getName() {
        return this.name;
    }

    /**
     * Method: getSurname
     *
     * @return String
     */
    public String getSurname() {
        return this.surname;
    }

    /**
     * Method: getId
     *
     * @return int
     */
    public int getId() {
        return this.id;
    }

    /**
     * Method: toString
     *
     * @return String
     */
    @NonNull
    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return "[" + this.id + "] " + this.name + " " + this.surname + "";
    }

    /**
     * Method: compareTo
     *
     * @param o
     * @return int
     */
    public int compareTo(@NonNull Patient o) {
        // ID order
        return Integer.compare(this.id, o.getId());
    }
}