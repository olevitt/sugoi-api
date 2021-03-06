package fr.insee.sugoi.services.controller.v1;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.insee.sugoi.converter.mapper.OuganextSugoiMapper;
import fr.insee.sugoi.converter.ouganext.Contact;
import fr.insee.sugoi.converter.ouganext.Contacts;
import fr.insee.sugoi.model.User;
import fr.insee.sugoi.model.Technique.MyPage;
import fr.insee.sugoi.sugoiapicore.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/v1")
@Tag(name = "Contact")
public class ContactController {

    @Autowired
    private UserService userService;

    @Autowired
    private OuganextSugoiMapper ouganextSugoiMapper;

    @GetMapping(produces = { MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_JSON_VALUE }, value = "/{domaine}/contact/{name}")
    public Contact searchContact(@PathVariable("domaine") String domaine, @PathVariable("name") String name)
            throws Exception {
        User user = userService.searchUser(domaine, name);
        Contact contact = ouganextSugoiMapper.serializeToOuganext(user, Contact.class);
        contact.setDomaineDeGestion(domaine.toLowerCase());
        return contact;
    }

    @GetMapping(produces = { MediaType.APPLICATION_XML_VALUE,
            MediaType.APPLICATION_JSON_VALUE }, value = "/{domaine}/contacts")
    public Contacts getMethodName(@RequestParam(name = "identifiant", required = false) String identifiant,
            @RequestParam(name = "nomCommun", required = false) String nomCommun,
            @RequestParam(name = "description", required = false) String description,
            @RequestParam(name = "organisationId", required = false) String organisationId,
            @PathVariable(name = "domaine") String domaineGestion,
            @RequestParam(name = "mail", required = false) String mail,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "start", defaultValue = "0") int offset,
            @RequestParam(name = "searchCookie", required = false) String searchCookie,
            @RequestParam(name = "typeRecherche", defaultValue = "et", required = true) String typeRecherche,
            @RequestParam(name = "habilitation", required = false) List<String> habilitations,
            @RequestParam(name = "application", required = false) String application,
            @RequestParam(name = "role", required = false) String role,
            @RequestParam(name = "rolePropriete", required = false) String rolePropriete,
            @RequestParam(name = "body", defaultValue = "false", required = false) boolean resultatsDansBody,
            @RequestParam(name = "idOnly", defaultValue = "false", required = false) boolean identifiantsSeuls,
            @RequestParam(name = "certificat", required = false) String certificat) {

        MyPage<User> users = userService.searchUsers(identifiant, nomCommun, description, organisationId,
                domaineGestion, mail, searchCookie, size, offset, typeRecherche, habilitations, application, role,
                rolePropriete, certificat);
        Contacts contacts = new Contacts();
        contacts.getListe()
                .addAll(users.getResults().stream()
                        .map(user -> ouganextSugoiMapper.serializeToOuganext(user, Contact.class))
                        .collect(Collectors.toList()));
        return contacts;
    }

}
