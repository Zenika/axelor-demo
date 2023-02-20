
package com.axelor.contact;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.axelor.contact.db.Contact;
import com.axelor.contact.db.Title;
import com.axelor.contact.db.repo.TitleRepository;
import com.axelor.data.ImportException;
import com.axelor.data.ImportTask;
import com.axelor.data.Importer;
import com.axelor.data.Listener;
import com.axelor.db.JPA;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;

import com.axelor.data.csv.CSVImporter;
import com.axelor.db.Model;
import com.axelor.test.GuiceModules;
import com.axelor.test.GuiceExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(GuiceExtension.class)
@GuiceModules({TestModule.class})
public class CSVTest {

    @Inject private Logger log;
    @Inject private TitleRepository titleRepo;

    //private Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void testCsvTitle() {

        final Importer importer = new CSVImporter("src/main/resources/data-demo/input-config.xml");

        final List<Title> records = new ArrayList<>();

        importer.addListener(new Listener() {
            @Override
            public void imported(Model bean) {
                log.info("Bean saved : {}(id={})",
                        bean.getClass().getSimpleName(),
                        bean.getId());
                if (bean instanceof Title) {
                    records.add((Title) bean);
                }
                else {
                    fail("Bean is not a Title");
                }
            }

            @Override
            public void imported(Integer total, Integer success) {
                log.info("Record (total): " + total);
                log.info("Record (success): " + success);
                assertEquals(success, total);
                assertEquals(3, success);
            }

            @Override
            public void handle(Model bean, Exception e) {

            }
        });

        importer.run(new ImportTask() {

            @Override
            public void configure() throws IOException {
                input("titles.csv", new File("src/main/resources/data-demo/input/titles.csv"));
            }

            @Override
            public boolean handle(ImportException exception) {
                log.error("Import error : " + exception);
                return false;
            }

            @Override
            public boolean handle(IOException e) {
                log.error("IOException error : " + e);
                return true;
            }
        });
        assertArrayEquals(records.stream().map(Title::getCode).toArray(), new String[]{"mr", "mrs", "miss"});
        log.error("End ");
    }

    @Test
    public void testCsvContact() {
        JPA.em().getTransaction().begin();
        if (titleRepo.findByCode("mr") == null) {
            Title title = new Title();
            title.setCode("mr");
            title.setName("Mr");
            titleRepo.save(title);
        }
        JPA.em().getTransaction().commit();
        JPA.em().clear();

        final Importer importer = new CSVImporter("src/main/resources/data-demo/input-config.xml", "src/main/resources/data-demo/input");

        final List<Contact> records = new ArrayList<>();

        importer.addListener(new Listener() {
            @Override
            public void imported(Model bean) {
                log.info("Bean saved : {}(id={})",
                        bean.getClass().getSimpleName(),
                        bean.getId());
                if (bean instanceof Contact) {
                    records.add((Contact) bean);
                }
                else {
                    fail("Bean is not a Contact");
                }
            }

            @Override
            public void imported(Integer total, Integer success) {
                log.info("Record (total): " + total);
                log.info("Record (success): " + success);
                assertEquals(success, total);
                assertEquals(2, success);
            }

            @Override
            public void handle(Model bean, Exception e) {

            }
        });

        importer.run(new ImportTask() {

            @Override
            public void configure() throws IOException {
                // input("titles.csv", new File("src/main/resources/data-demo/input/titles.csv"));
                input("contacts.csv", new File("src/main/resources/data-demo/input/contacts.csv"));
            }

            @Override
            public boolean handle(ImportException exception) {
                log.error("Import error : " + exception);
                return false;
            }

            @Override
            public boolean handle(IOException e) {
                log.error("IOException error : " + e);
                return true;
            }
        });
        assertArrayEquals(new String[]{"Mr Patrice De Saint Steban", "Mr Hugo Wood"}, records.stream().map(Contact::getFullName).toArray());
        log.error("End ");
    }
}