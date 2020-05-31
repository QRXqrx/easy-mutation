package practice;

import lombok.Data;
import nju.pa.experiment.util.IOUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.util.List;

/**
 * Learn how to use jsoup.
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-05-24
 */

@Data
public class JsoupTest {

    final String INDEX_HTML = "index.html";
    final String UTF8 = "UTF-8";



    @Test
    public void testParseMutant() {
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
                    while(mutantTr != null) {
                        System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

                        // Get lineNumber
                        int lineNumber = Integer.parseInt(mutantTr.select("td").get(0).select("a").get(0).ownText());
                        System.out.println("lineNumber: " + lineNumber);

                        // Get source code
                        String sourceCodeSelector = "a[href$=_" + lineNumber + "]";
                        Element aTag = doc.select(sourceCodeSelector).get(0);
                        Element sourceCodeTd = aTag.parent().parent().nextElementSibling();
                        Element sourceCodeSpan = sourceCodeTd.select("span").get(0);
                        String sourceCode = sourceCodeSpan.ownText().trim();
                        System.out.println("sourceCode: " + sourceCode);

                        // Get mutant description.
//                        Elements killedP = mutantTr.getElementsByAttributeValue("class", "KILLED");
                        Elements killedP = mutantTr.select("p[class=KILLED]");
                        Elements survivedP = mutantTr.select("p[class=SURVIVED]");

                        System.out.println("Number of Killed: " + killedP.size());
                        System.out.println("Number of Survived: " + survivedP.size());

                        System.out.println("Status: KILLED");
                        for (Element p : killedP) {
                            String description = p.ownText().substring(0, (p.ownText().indexOf("→") - 1));
                            String killedBy = p.select("span span").get(0).ownText();
                            System.out.println("description: " + description);
                            System.out.println("killedBy: " + killedBy);
                        }

                        System.out.println("Status: SURVIVED");
                        for (Element p : survivedP) {
                            String description = p.ownText();
                            String killedBy = p.select("span span").get(0).ownText();
                            System.out.println("description: " + description);
                            System.out.println("killedBy: " + killedBy);
                        }

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


    @Test
    public void testParseClassReport() {
        String dirPath = "material/pitest-report-example/RBT/net.mooctest";
        try {
            List<File> reports = IOUtil.listFilesOrEmpty(dirPath);

            for (File report : reports) {
                if(INDEX_HTML.equals(report.getName()))
                    continue;

                System.out.println("------------------------------------");

                try {
                    Document doc = Jsoup.parse(report, UTF8);

                    // Locate all mutants
                    Element trContainsMutations = doc.select("h2").get(0).parent().parent();
                    Element mutantTr = trContainsMutations.nextElementSibling();
                    // Collect all <p></p> that contains mutant information.
                    int numberOfMutants = 0;
                    while(mutantTr != null) {
                        Elements mutantPs = mutantTr.select("p");
                        numberOfMutants += mutantPs.size();
                        mutantTr = mutantTr.nextElementSibling();
                    }

                    System.out.println("numberOfMutants: " + numberOfMutants);



                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println("------------------------------------");
            }

        } catch (NotDirectoryException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testParseRBT() {
        String reportDirPath = "material/pitest-report-example/RBT/";
        File htmlFile = new File(reportDirPath, INDEX_HTML);

        try {

            Document doc = Jsoup.parse(htmlFile, UTF8);
            // Get the tr tag which contains coverage result.
            Element tr0 = doc.select("table").get(0)
                             .select("tbody").get(0)
                             .select("tr").get(0);
            // System.out.println(tr);
            int numberOfClasses = Integer.parseInt(tr0.select("td").get(0).ownText());
            // The td tag which contains coverage info
            Element coverageInfoTd = tr0.select("td").get(2);
            String coveragePercent = coverageInfoTd.ownText();
            String coverageRatio = coverageInfoTd.select("div").get(2).ownText();

            // Print to test
            System.out.println("numberOfClasses: " + numberOfClasses);
            System.out.println("coveragePercent: " + coveragePercent);
            System.out.println("coverageRatio: " + coverageRatio);

            // Get all packages under this project.
            Elements packageTrs = doc.select("table").get(1)
                                     .select("tbody").get(0)
                                     .select("tr");
            for (Element tr : packageTrs) {
                /*
                    This packageInfo has two functionality:
                        1. Get packageInfo as a attribute of mutant.
                        2. As path to reports of each classes, from which tool can get details of each mutant.
                */

                String packageInfo = tr.select("td").get(0).select("a").get(0).ownText();
                System.out.println("packageInfo: " + packageInfo);

                File packageDir = new File(reportDirPath, packageInfo);
                if(packageDir.exists()) {
                    if(packageDir.isDirectory()) {
                        File[] subReports = packageDir.listFiles();
                        if(subReports == null) {
                            System.out.println(packageDir + "is empty.");
                        } else {
                            for (File report : subReports) {
                                if(INDEX_HTML.equals(report.getName())) {
                                    continue;
                                }
                                System.out.println("------------------------");
                                System.out.println("report: " + report);
                                Document subDoc = Jsoup.parse(report, "UTF-8");
                                System.out.println(subDoc);
                                System.out.println("------------------------");
                            }
                        }
                    } else {
                        throw new RuntimeException(packageDir + "not a directory.");
                    }
                } else {
                    throw new RuntimeException("Package directory not exists.");
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Parse failed.");
        }




    }

}
