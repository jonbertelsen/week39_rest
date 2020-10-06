package facades;

import dtomappers.PersonDTO;
import dtomappers.PersonsDTO;
import entities.Address;
import entities.Person;
import exceptions.MissingInput;
import exceptions.PersonNotFound;
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
    public PersonDTO addPerson(PersonDTO p) throws MissingInput {
        if (isInputInvalid(p)){
           throw new MissingInput("First Name and/or Last Name is missing"); 
        }
        EntityManager em = getEntityManager();
        Person newPerson = new Person(p.getfName(), p.getlName(), p.getPhone());
        try {
            em.getTransaction().begin();
                addAddress(em, p, newPerson);
                em.persist(newPerson);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new PersonDTO(newPerson);
    }

    // Dette er en kommentar
    
    private void addAddress(EntityManager em, PersonDTO p, Person person) {
        Query query = em.createQuery("SELECT a FROM Address a WHERE a.street = :street AND a.zip = :zip AND a.city = :city");
        query.setParameter("street", p.getStreet());
        query.setParameter("zip", p.getZip());
        query.setParameter("city", p.getCity());
        List<Address> addresses = query.getResultList();
        if (alreadyExists(addresses)){
            person.setAddress(addresses.get(0));
        } else {
            person.setAddress(new Address(p.getStreet(), p.getZip(), p.getCity()));
        }
    }

    private static boolean alreadyExists(List<Address> addresses) {
        return addresses.size() > 0;
    }

    private static boolean isInputInvalid(PersonDTO p) {
        return (p.getfName().length() == 0) || (p.getlName().length() == 0);
    }

    @Override
    public PersonDTO deletePerson(long id) throws PersonNotFound {
         EntityManager em = getEntityManager();
          Person person = em.find(Person.class, id);
          if (person == null) {
            throw new PersonNotFound(String.format("Person with id: (%d) not found", id));
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
    public PersonDTO getPerson(long id) throws PersonNotFound {
       EntityManager em = getEntityManager();
       
           
       try {
           Person person = em.find(Person.class, id);
           if (person == null) {
                throw new PersonNotFound(String.format("Person with id: (%d) not found.", id));
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
    public PersonDTO editPerson(PersonDTO p) throws PersonNotFound, MissingInput {
        if (isInputInvalid(p)){
           throw new MissingInput("First Name and/or Last Name is missing"); 
        }
        EntityManager em = getEntityManager();

        try {
            em.getTransaction().begin();
            
                Person person = em.find(Person.class, p.getId());
                if (person == null) {
                    throw new PersonNotFound(String.format("Person with id: (%d) not found", p.getId()));
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
