/*
 * This file is generated by jOOQ.
 */
package revolut.home.task.account.tables.pojos;


import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.annotation.processing.Generated;


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
public class Transactions implements Serializable {

    private static final long serialVersionUID = 567333944;

    private final Long       id;
    private final Long       sender;
    private final Long       recipient;
    private final BigDecimal amount;
    private final Timestamp  sent;

    public Transactions(Transactions value) {
        this.id = value.id;
        this.sender = value.sender;
        this.recipient = value.recipient;
        this.amount = value.amount;
        this.sent = value.sent;
    }

    public Transactions(
        Long       id,
        Long       sender,
        Long       recipient,
        BigDecimal amount,
        Timestamp  sent
    ) {
        this.id = id;
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.sent = sent;
    }

    public Long getId() {
        return this.id;
    }

    public Long getSender() {
        return this.sender;
    }

    public Long getRecipient() {
        return this.recipient;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public Timestamp getSent() {
        return this.sent;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Transactions (");

        sb.append(id);
        sb.append(", ").append(sender);
        sb.append(", ").append(recipient);
        sb.append(", ").append(amount);
        sb.append(", ").append(sent);

        sb.append(")");
        return sb.toString();
    }
}
