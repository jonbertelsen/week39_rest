package facades;

import dtomappers.PersonDTO;
import dtomappers.PersonsDTO;
import exceptions.MissingInputException;
import exceptions.PersonNotFoundException;

/**
 *
 * @author jobe
 */
public interface IPersonFacade {
  public PersonDTO addPerson(String fName, String lName, String phone, String street, String zip, String city) throws MissingInputException;  
  public PersonDTO deletePerson(long id) throws PersonNotFoundException;
  public PersonDTO getPerson(long id) throws PersonNotFoundException; 
  public PersonsDTO getAllPersons();  
  public PersonDTO editPerson(PersonDTO p) throws PersonNotFoundException, MissingInputException ; 
}

