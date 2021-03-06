/*
 * This file is generated by jOOQ.
 */
package revolut.home.task.account.tables.records;


import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.annotation.processing.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;

import revolut.home.task.account.tables.Accounts;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class AccountsRecord extends UpdatableRecordImpl<AccountsRecord> implements Record4<Long, String, BigDecimal, Timestamp> {

    private static final long serialVersionUID = -1290197623;

    /**
     * Setter for <code>PUBLIC.ACCOUNTS.ID</code>.
     */
    public AccountsRecord setId(Long value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>PUBLIC.ACCOUNTS.ID</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>PUBLIC.ACCOUNTS.CURRENCY</code>.
     */
    public AccountsRecord setCurrency(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>PUBLIC.ACCOUNTS.CURRENCY</code>.
     */
    public String getCurrency() {
        return (String) get(1);
    }

    /**
     * Setter for <code>PUBLIC.ACCOUNTS.BALANCE</code>.
     */
    public AccountsRecord setBalance(BigDecimal value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>PUBLIC.ACCOUNTS.BALANCE</code>.
     */
    public BigDecimal getBalance() {
        return (BigDecimal) get(2);
    }

    /**
     * Setter for <code>PUBLIC.ACCOUNTS.CREATED</code>.
     */
    public AccountsRecord setCreated(Timestamp value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>PUBLIC.ACCOUNTS.CREATED</code>.
     */
    public Timestamp getCreated() {
        return (Timestamp) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row4<Long, String, BigDecimal, Timestamp> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    public Row4<Long, String, BigDecimal, Timestamp> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return Accounts.ACCOUNTS.ID;
    }

    @Override
    public Field<String> field2() {
        return Accounts.ACCOUNTS.CURRENCY;
    }

    @Override
    public Field<BigDecimal> field3() {
        return Accounts.ACCOUNTS.BALANCE;
    }

    @Override
    public Field<Timestamp> field4() {
        return Accounts.ACCOUNTS.CREATED;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getCurrency();
    }

    @Override
    public BigDecimal component3() {
        return getBalance();
    }

    @Override
    public Timestamp component4() {
        return getCreated();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getCurrency();
    }

    @Override
    public BigDecimal value3() {
        return getBalance();
    }

    @Override
    public Timestamp value4() {
        return getCreated();
    }

    @Override
    public AccountsRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public AccountsRecord value2(String value) {
        setCurrency(value);
        return this;
    }

    @Override
    public AccountsRecord value3(BigDecimal value) {
        setBalance(value);
        return this;
    }

    @Override
    public AccountsRecord value4(Timestamp value) {
        setCreated(value);
        return this;
    }

    @Override
    public AccountsRecord values(Long value1, String value2, BigDecimal value3, Timestamp value4) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached AccountsRecord
     */
    public AccountsRecord() {
        super(Accounts.ACCOUNTS);
    }

    /**
     * Create a detached, initialised AccountsRecord
     */
    public AccountsRecord(Long id, String currency, BigDecimal balance, Timestamp created) {
        super(Accounts.ACCOUNTS);

        set(0, id);
        set(1, currency);
        set(2, balance);
        set(3, created);
    }
}
