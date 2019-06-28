/*
 * This file is generated by jOOQ.
 */
package se4med.jooq.tables.records;


import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;

import se4med.jooq.tables.Doctorapp;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.2"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DoctorappRecord extends UpdatableRecordImpl<DoctorappRecord> implements Record2<String, String> {

    private static final long serialVersionUID = 413503165;

    /**
     * Setter for <code>se4med.doctorapp.emaildoctor</code>. questa tabella mette in relazione il dottore e le applicazioni.
Il dottore può consultare i dati delle applicazioni per le quali si è registrato

emaildoctor identifica il dottore -&gt; FK

se l'email della tabella doctor viene cancellata/modificata, si cancellano/modificano tutti i record che contengono quel valore
     */
    public void setEmaildoctor(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>se4med.doctorapp.emaildoctor</code>. questa tabella mette in relazione il dottore e le applicazioni.
Il dottore può consultare i dati delle applicazioni per le quali si è registrato

emaildoctor identifica il dottore -&gt; FK

se l'email della tabella doctor viene cancellata/modificata, si cancellano/modificano tutti i record che contengono quel valore
     */
    public String getEmaildoctor() {
        return (String) get(0);
    }

    /**
     * Setter for <code>se4med.doctorapp.idapp</code>. identifica l'applicazione per la quale il dottore è abilitato a consultare i dati -&gt; FK

se l'id della tabella application viene aggiornato, viene aggiornato anche idapp
se l'id della tabella application viene cancellato e c'è un record che contiene quell'id, non viene permessa la cancellazione dell'applicazione
     */
    public void setIdapp(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>se4med.doctorapp.idapp</code>. identifica l'applicazione per la quale il dottore è abilitato a consultare i dati -&gt; FK

se l'id della tabella application viene aggiornato, viene aggiornato anche idapp
se l'id della tabella application viene cancellato e c'è un record che contiene quell'id, non viene permessa la cancellazione dell'applicazione
     */
    public String getIdapp() {
        return (String) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record2<String, String> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row2<String, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row2<String, String> valuesRow() {
        return (Row2) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return Doctorapp.DOCTORAPP.EMAILDOCTOR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return Doctorapp.DOCTORAPP.IDAPP;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component1() {
        return getEmaildoctor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getIdapp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getEmaildoctor();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getIdapp();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DoctorappRecord value1(String value) {
        setEmaildoctor(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DoctorappRecord value2(String value) {
        setIdapp(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DoctorappRecord values(String value1, String value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached DoctorappRecord
     */
    public DoctorappRecord() {
        super(Doctorapp.DOCTORAPP);
    }

    /**
     * Create a detached, initialised DoctorappRecord
     */
    public DoctorappRecord(String emaildoctor, String idapp) {
        super(Doctorapp.DOCTORAPP);

        set(0, emaildoctor);
        set(1, idapp);
    }
}
