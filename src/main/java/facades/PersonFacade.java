package facades;

import dtomappers.PersonDTO;
import dtomappers.PersonsDTO;
import entities.Address;
import entities.Person;
import exceptions.MissingInputException;
import exceptions.PersonNotFoundException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 *
 * Rename Class to a relevant name Add add relevant facade methods
 */
public class PersonFacade implements IPersonFacade {

    private static PersonFacade instance;
    private static EntityManagerFactory emf;
    
    //Private Constructor to ensure Singleton
    private PersonFacade() {}
    
    
    /**
     * 
     * @param _emf
     * @return an instance of this facade class.
     */
    public static PersonFacade getFacadeExample(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PersonFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public long getPersonCount(){
        EntityManager em = getEntityManager();
        try {
            long personCount = (long) em.createQuery("SELECT COUNT(r) FROM Person r").getSingleResult();
            return personCount;
        } finally{  
            em.close();
        } 
    }

    @Override
    public PersonDTO addPerson(String fName, String lName, String phone, String street, String zip, String city) throws MissingInputException {
        if ((fName.length() == 0) || (lName.length() == 0)){
           throw new MissingInputException("First Name and/or Last Name is missing"); 
        }
        EntityManager em = getEntityManager();
        Person person = new Person(fName, lName, phone);

        try {
            em.getTransaction().begin();
                Query query = em.createQuery("SELECT a FROM Address a WHERE a.street = :street AND a.zip = :zip AND a.city = :city");
                query.setParameter("street", street);
                query.setParameter("zip", zip);
                query.setParameter("city", city);
                List<Address> addresses = query.getResultList();
                if (addresses.size() > 0){
                    person.setAddress(addresses.get(0)); // The address already exists
                } else {
                    person.setAddress(new Address(street,zip,city));
                }
                em.persist(person);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new PersonDTO(person);
    }

    @Override
    public PersonDTO deletePerson(long id) throws PersonNotFoundException {
         EntityManager em = getEntityManager();
          Person person = em.find(Person.class, id);
          if (person == null) {
            throw new PersonNotFoundException(String.format("Person with id: (%d) not found", id));
          } else {
                try {
                    em.getTransaction().begin();
                        em.remove(person);
                    em.getTransaction().commit();
                } finally {
                    em.close();
            }
            return new PersonDTO(person);
          }
    }

    @Override
    public PersonDTO getPerson(long id) throws PersonNotFoundException {
       EntityManager em = getEntityManager();
       
           
       try {
           Person person = em.find(Person.class, id);
           if (person == null) {
                throw new PersonNotFoundException(String.format("Person with id: (%d) not found.", id));
            } else {
                return new PersonDTO(person);
           }
       } finally {
           em.close();
       }
    }

    @Override
    public PersonsDTO getAllPersons() {
      EntityManager em = getEntityManager();
        try {
            return new PersonsDTO(em.createNamedQuery("Person.getAllRows").getResultList());
        } finally{  
            em.close();
        }   
    }

    @Override
    public PersonDTO editPerson(PersonDTO p) throws PersonNotFoundException, MissingInputException {
        if ((p.getfName().length() == 0) || (p.getlName().length() == 0)){
           throw new MissingInputException("First Name and/or Last Name is missing"); 
        }
        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();
            
                Person person = em.find(Person.class, p.getId());
                if (person == null) {
                    throw new PersonNotFoundException(String.format("Person with id: (%d) not found", p.getId()));
                } else {
                    person.setFirstName(p.getfName());
                    person.setLastName(p.getlName());
                    person.setPhone(p.getPhone());
                    person.setLastEdited();
                    
                    TypedQuery personAtSameAddressQuery = em.createQuery("SELECT p FROM Person p WHERE p.address.id = :a_id AND p.id <> :p_id", Address.class);
                    personAtSameAddressQuery.setParameter("a_id", person.getAddress().getId());
                    personAtSameAddressQuery.setParameter("p_id", person.getId());
                    List<Person> personsAtSameAddress = personAtSameAddressQuery.getResultList();
                    
                    if (personsAtSameAddress.isEmpty()){
                        person.getAddress().setStreet(p.getStreet());
                        person.getAddress().setZip(p.getZip());
                        person.getAddress().setCity(p.getCity());
                    } else {
                        Address newAddress = new Address(p.getStreet(), p.getZip(),p.getCity());
                        person.removeAddress(person.getAddress());
                        person.setAddress(newAddress);
                    }
                    }
                em.getTransaction().commit();
                return new PersonDTO(person);
        } finally {  
          em.close();
        }
        
        
    }

}
