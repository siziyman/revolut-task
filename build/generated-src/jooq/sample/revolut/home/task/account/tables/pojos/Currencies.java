/*
 * This file is generated by jOOQ.
 */
package revolut.home.task.account.tables.pojos;


import java.io.Serializable;

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
public class Currencies implements Serializable {

    private static final long serialVersionUID = 913034964;

    private final String id;
    private final String fullName;

    public Currencies(Currencies value) {
        this.id = value.id;
        this.fullName = value.fullName;
    }

    public Currencies(
        String id,
        String fullName
    ) {
        this.id = id;
        this.fullName = fullName;
    }

    public String getId() {
        return this.id;
    }

    public String getFullName() {
        return this.fullName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Currencies (");

        sb.append(id);
        sb.append(", ").append(fullName);

        sb.append(")");
        return sb.toString();
    }
}