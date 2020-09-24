package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtomappers.PersonDTO;
import dtomappers.PersonsDTO;
import exceptions.MissingInputException;
import exceptions.PersonNotFoundException;
import utils.EMF_Creator;
import facades.PersonFacade;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

//Todo Remove or change relevant parts before ACTUAL use
@Path("person")
public class PersonResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    
    //An alternative way to get the EntityManagerFactory, whithout having to type the details all over the code
    //EMF = EMF_Creator.createEntityManagerFactory(DbSelector.DEV, Strategy.CREATE);
    
    private static final PersonFacade FACADE =  PersonFacade.getFacadeExample(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
            
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String serverIsUp() {
        return "{\"msg\":\"Your Person API is up and running\"}";
    }
    
    @Path("{id}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getPerson(@PathParam("id") long id ) throws PersonNotFoundException {
        PersonDTO p = FACADE.getPerson(id);
        
        return GSON.toJson(p);
    }
    
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public String addPerson(String person) throws MissingInputException {
        PersonDTO p = GSON.fromJson(person, PersonDTO.class);
        PersonDTO pNew = FACADE.addPerson(p.getfName(), p.getlName(), p.getPhone(), p.getStreet(), p.getZip(), p.getCity());
        return GSON.toJson(pNew);
    }
    
    @PUT
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public String updatePerson(@PathParam("id") long id,  String person) throws PersonNotFoundException, MissingInputException {
        PersonDTO pDTO = GSON.fromJson(person, PersonDTO.class);
        pDTO.setId(id);
        PersonDTO pNew = FACADE.editPerson(pDTO);
        return GSON.toJson(pNew);
    }
    
    @DELETE
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public String deletePerson(@PathParam("id") long id) throws PersonNotFoundException {
        PersonDTO pDeleted = FACADE.deletePerson(id);
        return GSON.toJson(pDeleted);
    }
    
    @Path("all")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getAll() {
        PersonsDTO persons = FACADE.getAllPersons();
        return GSON.toJson(persons);
    }
    
    @Path("count")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getPersonCount() {
        long count = FACADE.getPersonCount();
        return "{\"count\":"+count+"}";  
    }
}
