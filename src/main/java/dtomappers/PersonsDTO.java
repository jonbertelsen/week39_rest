package dtomappers;

import entities.Person;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jobe
 */
public class PersonsDTO {
    private List<PersonDTO> all = new ArrayList();

    public PersonsDTO(List<Person> personEntities) {
        personEntities.forEach((p) -> {
            all.add(new PersonDTO(p));
        });
    }

    public List<PersonDTO> getAll() {
        return all;
    }
    
}
