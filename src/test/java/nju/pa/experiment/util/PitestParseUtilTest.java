package nju.pa.experiment.util;

import nju.pa.experiment.data.mutation.MutantInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-05-25
 */
public class PitestParseUtilTest {

    final String INDEX_HTML = "index.html";
    final String UTF8 = "UTF-8";

    @Test
    public void testParseClassReport1() {
        String packageInfo = "net.mooctest";
        String dirPath = "material/pitest-report-example/RBT/net.mooctest";

        try {
            List<File> reports = IOUtil.listFilesOrEmpty(dirPath)
                    .stream().filter((file) -> !INDEX_HTML.equals(file.getName())).collect(Collectors.toList());

            for (File report : reports) {
                System.out.println("---------------------------------");
                System.out.println(PitestParseUtil.parseClassReport(report, packageInfo));
                System.out.println("---------------------------------");
            }

        } catch (NotDirectoryException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testParseSummaryReport() {
        File summaryReport = new File("material/pitest-report-example/RBT", INDEX_HTML);
        System.out.println(PitestParseUtil.parse(summaryReport));
    }

    @Test
    public void testParseMutant1() {
        String dirPath = "material/pitest-report-example/RBT/net.mooctest";
        try {
            List<File> reports = IOUtil.listFilesOrEmpty(dirPath);

            for (File report : reports) {
                if(INDEX_HTML.equals(report.getName()))
                    continue;

                System.out.println("------------------------------------");

                try {
                    Document doc = Jsoup.parse(report, UTF8);

                    // Get fileName
                    String className = doc.select("h1").get(0).ownText();
                    System.out.println("fileName: " + className);

                    // Locate all mutants
                    Element trContainsMutations = doc.select("h2").get(0).parent().parent();
                    Element mutantTr = trContainsMutations.nextElementSibling();
                    // Collect all <p></p> that contains mutant information.
                    int numberOfMutants = 0;
                    while(mutantTr != null) {
                        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                        List<MutantInfo> mutantInfos = PitestParseUtil.parseMutant(mutantTr, doc);

                        if(mutantInfos.isEmpty()) {
                            System.out.println("!@$@$^@$&#%^*#%^*#^*");
                            System.out.println(mutantTr);
                            System.out.println("!@$@$^@$&#%^*#%^*#^*");
                        }


                        for (MutantInfo mutantInfo : mutantInfos)
                            System.out.println(mutantInfo);


                        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                        mutantTr = mutantTr.nextElementSibling();
                    }




                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println("------------------------------------");
            }

        } catch (NotDirectoryException e) {
            e.printStackTrace();
        }

    }
}
