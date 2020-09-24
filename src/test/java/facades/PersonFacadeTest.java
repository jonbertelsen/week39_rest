package facades;

import dtomappers.PersonDTO;
import dtomappers.PersonsDTO;
import entities.Address;
import utils.EMF_Creator;
import entities.Person;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//Uncomment the line below, to temporarily disable this test
//@Disabled
public class PersonFacadeTest {

    private static EntityManagerFactory emf;
    private static PersonFacade facade;
    private static Person p1, p2, p3;

    public PersonFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
       emf = EMF_Creator.createEntityManagerFactoryForTest();
       facade = PersonFacade.getFacadeExample(emf);
    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the script below to use YOUR OWN entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager(); 
        p1 = new Person("Jønke", "Jensen", "1212122");
        p1.setAddress(new Address("Ndr Frihavnsgade 29","2100","Kbh Ø"));
        p2 = new Person("Jørgen", "Fehår", "3232222");
        p2.setAddress(new Address("Østerbrogade 2", "2200","Kbh N"));
        p3 = new Person("Blondie", "Jensen", "323232");
        p3.setAddress(new Address("Storegade 3","3700","Rønne"));

        try {
            em.getTransaction().begin();
                em.createNamedQuery("Person.deleteAllRows").executeUpdate(); 
                em.createNamedQuery("Address.deleteAllRows").executeUpdate();
                em.persist(p1);
                em.persist(p2);
                em.persist(p3);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    
    @Test
    

    @AfterEach
    public void tearDown() {
//        Remove any data after each test was run
    }

    // TODO: Delete or change this method 
    @Test
    public void testAFacadeMethod() {
        assertEquals(3, facade.getPersonCount(), "Expects two rows in the database");
    }

    @Test
    public void testGetFacadeExample() {
        System.out.println("getFacadeExample");
        EntityManagerFactory _emf = null;
        PersonFacade expResult = null;
        PersonFacade result = PersonFacade.getFacadeExample(_emf);
        assertNotEquals(expResult, result);
    }

    @Test
    public void testGetPersonCount() {
        System.out.println("getPersonCount");
        EntityManagerFactory _emf = null;
        PersonFacade instance = PersonFacade.getFacadeExample(_emf);
        long expResult = 3L;
        long result = instance.getPersonCount();
        assertEquals(expResult, result);
    }

    @Test
    public void testAddPerson() throws Exception {
        System.out.println("addPerson");
        String fName = "Aria";
        String lName = "Stark";
        String phone = "234234";
        String street = "The Hollow";
        String zip = "2332";
        String city = "Winterfell";
        EntityManagerFactory _emf = null;
        PersonFacade instance = PersonFacade.getFacadeExample(_emf);
        PersonDTO result = instance.addPerson(fName, lName, phone, street, zip, city);
        PersonDTO expResult = new PersonDTO(fName, lName, phone, street, zip, city);
        expResult.setId(expResult.getId());
        assertEquals(expResult.getfName(), result.getfName());
        assertEquals(expResult.getlName(), result.getlName());
        assertEquals(expResult.getPhone(), result.getPhone());
    }

    @Test
    public void testDeletePerson() throws Exception {
        System.out.println("deletePerson");
        long id = p2.getId();
        EntityManagerFactory _emf = null;
        PersonFacade instance = PersonFacade.getFacadeExample(_emf);
        PersonDTO expResult = new PersonDTO(p2);
        PersonDTO result = instance.deletePerson(id);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetPerson() throws Exception {
        System.out.println("getPerson");
        long id = p3.getId();
        EntityManagerFactory _emf = null;
        PersonFacade instance = PersonFacade.getFacadeExample(_emf);
        PersonDTO expResult = new PersonDTO(p3);
        PersonDTO result = instance.getPerson(id);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetAllPersons() {
        System.out.println("getAllPersons");
        EntityManagerFactory _emf = null;
        PersonFacade instance = PersonFacade.getFacadeExample(_emf);
        int expResult = 3;
        PersonsDTO result = instance.getAllPersons();
        assertEquals(expResult, result.getAll().size());
        PersonDTO p1DTO = new PersonDTO(p1);
        PersonDTO p2DTO = new PersonDTO(p2);
        PersonDTO p3DTO = new PersonDTO(p3);
        assertThat(result.getAll(), containsInAnyOrder(p1DTO, p2DTO, p3DTO));
    }

    @Test
    public void testEditPerson() throws Exception {
        System.out.println("editPerson");
        PersonDTO p = new PersonDTO(p1);
        EntityManagerFactory _emf = null;
        PersonFacade instance = PersonFacade.getFacadeExample(_emf);
        PersonDTO expResult = new PersonDTO(p1);
        expResult.setfName("Birger");
        p.setfName("Birger");
        PersonDTO result = instance.editPerson(p);
        assertEquals(expResult.getfName(), result.getfName());
    }

}
