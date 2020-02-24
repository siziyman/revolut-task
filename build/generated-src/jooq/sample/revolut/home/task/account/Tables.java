/*
 * This file is generated by jOOQ.
 */
package revolut.home.task.account;


import javax.annotation.processing.Generated;

import revolut.home.task.account.tables.Accounts;
import revolut.home.task.account.tables.Currencies;
import revolut.home.task.account.tables.Transactions;


/**
 * Convenience access to all tables in PUBLIC
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.4"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tables {

    /**
     * The table <code>PUBLIC.ACCOUNTS</code>.
     */
    public static final Accounts ACCOUNTS = Accounts.ACCOUNTS;

    /**
     * The table <code>PUBLIC.CURRENCIES</code>.
     */
    public static final Currencies CURRENCIES = Currencies.CURRENCIES;

    /**
     * The table <code>PUBLIC.TRANSACTIONS</code>.
     */
    public static final Transactions TRANSACTIONS = Transactions.TRANSACTIONS;
}
