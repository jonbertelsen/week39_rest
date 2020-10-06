package facades;

import dtomappers.PersonDTO;
import dtomappers.PersonsDTO;
import exceptions.MissingInput;
import exceptions.PersonNotFound;

/**
 *
 * @author jobe
 */
public interface IPersonFacade {
  public PersonDTO addPerson(PersonDTO p) throws MissingInput;  
  public PersonDTO deletePerson(long id) throws PersonNotFound;
  public PersonDTO getPerson(long id) throws PersonNotFound; 
  public PersonsDTO getAllPersons();  
  public PersonDTO editPerson(PersonDTO p) throws PersonNotFound, MissingInput ; 
}

