// core/db/Flight.java
package core.db;

import core.db.annotations.Entity;
import core.db.annotations.Column;
import core.db.annotations.Id;

@Entity(table = "flights")
public class Flight {
    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "base_price")
    private double basePrice;

}