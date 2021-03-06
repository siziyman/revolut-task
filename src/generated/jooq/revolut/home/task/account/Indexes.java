/*
 * This file is generated by jOOQ.
 */
package revolut.home.task.account;


import javax.annotation.processing.Generated;

import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.Internal;

import revolut.home.task.account.tables.Accounts;
import revolut.home.task.account.tables.Currencies;
import revolut.home.task.account.tables.Transactions;


/**
 * A class modelling indexes of tables of the <code>PUBLIC</code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Indexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index CONSTRAINT_INDEX_A = Indexes0.CONSTRAINT_INDEX_A;
    public static final Index PRIMARY_KEY_A = Indexes0.PRIMARY_KEY_A;
    public static final Index PRIMARY_KEY_D = Indexes0.PRIMARY_KEY_D;
    public static final Index CONSTRAINT_INDEX_F = Indexes0.CONSTRAINT_INDEX_F;
    public static final Index CONSTRAINT_INDEX_FE = Indexes0.CONSTRAINT_INDEX_FE;
    public static final Index PRIMARY_KEY_F = Indexes0.PRIMARY_KEY_F;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Indexes0 {
        public static Index CONSTRAINT_INDEX_A = Internal.createIndex("CONSTRAINT_INDEX_A", Accounts.ACCOUNTS, new OrderField[] { Accounts.ACCOUNTS.CURRENCY }, false);
        public static Index PRIMARY_KEY_A = Internal.createIndex("PRIMARY_KEY_A", Accounts.ACCOUNTS, new OrderField[] { Accounts.ACCOUNTS.ID }, true);
        public static Index PRIMARY_KEY_D = Internal.createIndex("PRIMARY_KEY_D", Currencies.CURRENCIES, new OrderField[] { Currencies.CURRENCIES.ID }, true);
        public static Index CONSTRAINT_INDEX_F = Internal.createIndex("CONSTRAINT_INDEX_F", Transactions.TRANSACTIONS, new OrderField[] { Transactions.TRANSACTIONS.SENDER }, false);
        public static Index CONSTRAINT_INDEX_FE = Internal.createIndex("CONSTRAINT_INDEX_FE", Transactions.TRANSACTIONS, new OrderField[] { Transactions.TRANSACTIONS.RECIPIENT }, false);
        public static Index PRIMARY_KEY_F = Internal.createIndex("PRIMARY_KEY_F", Transactions.TRANSACTIONS, new OrderField[] { Transactions.TRANSACTIONS.ID }, true);
    }
}
