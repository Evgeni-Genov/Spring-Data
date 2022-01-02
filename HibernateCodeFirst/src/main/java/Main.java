import entity.Product;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.math.BigDecimal;
import java.text.ParseException;

public class Main {
    public static void main(String[] args) throws ParseException {
        EntityManager entityManager = Persistence
                .createEntityManagerFactory("test")
                .createEntityManager();



    }
}
