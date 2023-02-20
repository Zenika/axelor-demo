
package com.axelor.contact;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.axelor.data.ImportException;
import com.axelor.data.ImportTask;
import com.axelor.data.Importer;
import com.axelor.data.Listener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.axelor.data.csv.CSVImporter;
import com.axelor.db.Model;
import com.axelor.test.GuiceModules;
import com.axelor.test.GuiceRunner;
import com.google.inject.Injector;
import com.axelor.test.GuiceExtension;

@ExtendWith(GuiceExtension.class)
@GuiceModules({TestModule.class})
public class CSVTest {

    @Inject private Logger log;

    //private Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void testCsv() throws ClassNotFoundException {

        final Importer importer = new CSVImporter("src/main/resources/data-demo/input-config.xml");

        final List<Model> records = new ArrayList<>();
        final Map<String, Object> context = new HashMap<>();

        context.put("CUSTOMER_PHONE", "+3326253225");

        importer.addListener(new Listener() {
            @Override
            public void imported(Model bean) {
                log.info("Bean saved : {}(id={})",
                        bean.getClass().getSimpleName(),
                        bean.getId());
                records.add(bean);
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
    }
}