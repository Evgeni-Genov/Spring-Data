import entities.Address;
import entities.Employee;
import entities.Project;
import entities.Town;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Engine implements Runnable {

    private final EntityManager entityManager;
    private BufferedReader bufferedReader;

    public Engine(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    }


    @Override
    public void run() {
        System.out.println("Please the enter the exercise number:");

        try {
            int exNum = Integer.parseInt(bufferedReader.readLine());

            switch (exNum) {
                case 2 -> exerciseTwo();
                case 3 -> exerciseThree();
                case 4 -> exerciseFour();
                case 5 -> exerciseFive();
                case 6 -> exerciseSix();
                case 7 -> exerciseSeven();
                case 8 -> exerciseEight();
                case 9 -> exerciseNine();
                case 10 -> exerciseTen();
                case 11 -> exerciseEleven();
                case 12 -> exerciseTwelve();
                case 13 -> exerciseThirteen();

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            entityManager.close();
        }

    }


    private void exerciseTwo() {
        entityManager.getTransaction().begin();
        Query query = entityManager.createQuery("UPDATE Town t " +
                "SET t.name = upper(t.name) " +
                "WHERE length(t.name) >= 5 ");

        System.out.println(query.executeUpdate());

        entityManager.getTransaction().commit();
    }

    private void exerciseThree() throws IOException {
        System.out.println("Enter employee full name:");
        String[] fullName = bufferedReader.readLine().split("\\s+");
        String firstName = fullName[0];
        String lastName = fullName[1];

        Long singleResult = entityManager.createQuery("SELECT count(e) FROM Employee e " +
                        "WHERE e.firstName = :f_name AND e.lastName = :l_name", Long.class)
                .setParameter("f_name", firstName)
                .setParameter("l_name", lastName)
                .getSingleResult();

        System.out.println(singleResult == 0
                ? "No" : "Yes");
    }

    private void exerciseFour() {
        entityManager.createQuery("SELECT e FROM Employee e " +
                        "WHERE e.salary > :min_salary", Employee.class)
                .setParameter("min_salary", BigDecimal.valueOf(50000L))
                .getResultStream()
                .map(Employee::getFirstName)
                .forEach(System.out::println);
    }

    private void exerciseFive() {
        entityManager
                .createQuery("SELECT e FROM Employee e " +
                        "WHERE e.department.name = :d_name " +
                        "ORDER BY e.salary, e.id", Employee.class)
                .setParameter("d_name", "Research and Development")
                .getResultStream()
                .forEach(employee -> {
                    System.out.printf("%s %s from %s - $%.2f%n",
                            employee.getFirstName(), employee.getLastName(),
                            employee.getDepartment().getName(), employee.getSalary());
                });
    }

    private void exerciseSix() throws IOException {
        System.out.println("Please enter the last name of the employee:");
        String lastName = bufferedReader.readLine();

        Employee employee = entityManager.createQuery("SELECT e FROM Employee e " +
                        "WHERE e.lastName = :l_name", Employee.class)
                .setParameter("l_name", lastName)
                .getSingleResult();


        Address address = createAddress("Vitoshka 15");

        entityManager.getTransaction().begin();
        employee.setAddress(address);
        entityManager.getTransaction().commit();
    }

    private Address createAddress(String addressText) {
        Address address = new Address();
        address.setText(addressText);

        entityManager.getTransaction().begin();
        entityManager.persist(address);
        entityManager.getTransaction().commit();

        return address;
    }

    private void exerciseSeven() {
        List<Address> addresses = entityManager
                .createQuery("SELECT a FROM Address a " +
                        "ORDER BY a.employees.size DESC", Address.class)
                .setMaxResults(10)
                .getResultList();

        addresses.forEach(address -> {
            System.out.printf("%s, %s - %d employees%n",
                    address.getText(),
                    address.getTown() == null
                            ? "Unknown" : address.getTown().getName(),
                    address.getEmployees().size());
        });


    }

    private void exerciseEight() throws IOException {
        System.out.println("Please enter the id of employee:");
        int id = Integer.parseInt(this.bufferedReader.readLine());
        Employee employee = (Employee) this.entityManager
                .createQuery("SELECT e FROM Employee e WHERE e.id = :empId", Employee.class)
                .setParameter("empId", id).getSingleResult();
        StringBuilder sb = new StringBuilder();

        sb.append(employee.getFirstName()).append(" ")
                .append(employee.getLastName()).append(" - ")
                .append(employee.getJobTitle()).append("\n\t");

        employee.getProjects()
                .stream()
                .map(Project::getName)
                .sorted().forEach((p) -> {
                    sb.append(p).append("\n\t");
                });

        System.out.println(sb);
    }

    private void exerciseNine() {
        List<Project> result = this.entityManager
                .createQuery("SELECT p FROM Project p " +
                        "ORDER BY p.startDate DESC", Project.class)
                .setMaxResults(10).getResultList();

        result
                .stream()
                .sorted(Comparator.comparing(Project::getName))
                .forEach((p) -> {
                    System.out.printf("Project name: %s%n\t" +
                                    "Project Description: %s%n\t" +
                                    "Project Start Date:%s%n\t" +
                                    "Project End Date:%s%n", p.getName(), p.getDescription(),
                            p.getStartDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                            p.getEndDate());
                });
    }

    private void exerciseTen() {
        entityManager.getTransaction().begin();
        int affectedRows = entityManager.createQuery("UPDATE Employee e " +
                        "SET e.salary = e.salary*1.2 " +
                        "WHERE e.department.id IN :ids")
                .setParameter("ids", Set.of(1, 2, 4, 11))
                .executeUpdate();

        entityManager.getTransaction().commit();
        System.out.println(affectedRows);

    }

    private void exerciseEleven() throws IOException {
        System.out.println("Please enter the pattern:");
        String pattern = this.bufferedReader.readLine();

        this.entityManager
                .createQuery("SELECT e FROM  Employee e " +
                        "WHERE e.firstName LIKE :patt", Employee.class)
                .setParameter("patt", pattern + "%")
                .getResultStream().forEach((e) -> {
                    System.out.printf("%s %s - %s - (%.2f)%n", e.getFirstName(),
                            e.getLastName(), e.getJobTitle(), e.getSalary());
                });
    }

    private void exerciseTwelve() {
        List<Object[]> rows = entityManager
                .createNativeQuery("SELECT d.name, MAX(e.salary) AS `m_salary` FROM departments d " +
                        "join employees e on d.department_id = e.department_id\n" +
                        "GROUP BY d.name\n" +
                        "HAVING `m_salary` NOT BETWEEN 30000 AND 70000;")
                .getResultList();
    }

    private void exerciseThirteen() throws IOException {
        System.out.println("Please enter the name of the town:");

        String townName = this.bufferedReader.readLine();
        Town town = (Town) this.entityManager
                .createQuery("SELECT t FROM Town t " +
                        "WHERE t.name = :tName", Town.class)
                .setParameter("tName", townName).getSingleResult();

        int addresses = this.getDeletedAddressesByTownId(town.getId());
        this.entityManager.getTransaction().begin();
        this.entityManager.remove(town);
        this.entityManager.getTransaction().commit();

        System.out.printf("%d %s in %s deleted", addresses, addresses > 1 ? "addresses" : "address", townName);
    }

    private int getDeletedAddressesByTownId(Integer id) {
        List<Address> addresses = this.entityManager
                .createQuery("SELECT a FROM Address a " +
                        "WHERE a.town.id = :tId", Address.class)
                .setParameter("tId", id).getResultList();

        this.entityManager.getTransaction().begin();
        EntityManager var10001 = this.entityManager;
        Objects.requireNonNull(var10001);
        addresses.forEach(var10001::remove);
        this.entityManager.getTransaction().commit();

        return addresses.size();
    }

}
